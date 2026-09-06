# [계획서] 앱 초기 데이터 갱신 로직 개선 (RefreshAppStartData)

- **작성 일자**: 2026-09-06
- **기준 브랜치**: develop (최신)
- **작업 브랜치**: feat/improve_app_sync_logic (또는 improve_app_sync_logic)
- **문서 상태**: 검토 대기 (승인 전 구현 금지)

---

## 1. 개발 목표 및 배경

### 1.1 현상 및 배경
- 사용자가 앱을 처음 켜거나 새로운 버전이 나왔을 때, 스플래시 화면(IntroScreen)에서 데이터를 받아온 직후 상단 버전 선택 바를 눌러 다이얼로그를 열었을 때:
  - 새롭게 추가된 아이템/챔피언 아이콘과 'NEW' 뱃지가 즉시 나타나지 않고 단순 버전 이름만 노출됨.
  - 이후 시간이 지나거나, 앱을 완전히 종료했다가 다시 켰을 때 비로소 새 챔피언/아이템 정보가 정상 표시됨.

### 1.2 핵심 목적
- 스플래시 화면 단계에서 초기 동기화(`RefreshAppStartData`)가 끝나는 즉시, 첫 진입 시점부터 상단 버전 선택 다이얼로그에 신규 챔피언 및 아이템 정보가 누락 없이 즉각 100% 반영되도록 개선.
- 비동기 Flow 캐시 불일치로 인한 타이밍 이슈 및 불필요한 반복 갱신 방지.

---

## 2. 현행 코드 분석 및 원인 규명 (Root Cause Analysis)

### 2.1 원인 1: `versionRepository.allVersionList.firstOrNull()`의 Stale Cache 참조 (핵심 원인)
- **위치**: `core/domain/.../RefreshAppStartData.kt` 79번째 줄
- **동작**:
  1. 1단계에서 `getNotInitCompleteVersionList()`로 미완료 버전(예: 최신 버전 `14.20.1`)의 챔피언/아이템/스펠/룬 데이터를 받아와 DB에 `isCompleteChampions = true, isCompleteItems = true`로 저장(`versionRepository.updateVersionData`).
  2. 직후 2단계에서 이전 버전과의 비교를 위해 `versionRepository.allVersionList.firstOrNull()`을 호출.
  3. 그러나 `allVersionList`는 `StateFlow`이며, Room DB 변경 사항이 Flow를 거쳐 StateFlow의 캐시(`value`)에 갱신되기까지는 비동기 지연(`Dispatchers.IO`)이 발생함.
  4. 따라서 `firstOrNull()` 호출 시점에는 **1단계 갱신 전의 과거 데이터(`isCompleteChampions = false`)** 가 반환됨.
  5. 그 결과, 82~83번째 줄:
     ```kotlin
     if (!version.isCompleteChampions || !version.isCompleteItems)
         return@async
     ```
     이 조건에 걸려 최신 버전의 `getNewChampionIdList` / `getNewItemIdList` 연산이 통째로 **Skip(생략)** 됨.
  6. `RefreshAppStartData`가 곧바로 종료되고 `_isInitComplete.emit(true)`가 호출되어 스플래시가 닫힘.
  7. 다이얼로그를 열었을 때 `newChampionIdList = null, newItemIdList = null`이므로 단순 버전만 표시됨.

### 2.2 원인 2: "다시 껐다 켰을 때" 또는 "시간이 지난 후" 뒤늦게 표시되는 이유
- **재실행 시**: 1단계의 `isCompleteChampions = true`는 이미 SQLite DB에 저장되어 있으므로, 앱을 껐다 켜면 시작 시점부터 StateFlow에 `isCompleteChampions = true`가 적재됨. 이로 인해 두 번째 실행 때 비로소 2단계 조건문을 통과하여 신규 목록이 계산·저장됨.
- **시간 경과 시**: 다수의 버전 업데이트로 인한 Room Flow의 연속 재발행 또는 화면 전환/수명주기 재구독 과정에서 뒤늦게 갱신된 리스트가 흘러들어옴.

### 2.3 원인 3: Stale Entity 덮어쓰기로 인한 데이터 롤백 위험
- 110번째 줄에서 `versionRepository.updateVersionData(version.copy(newChampionIdList = ...))`를 호출할 때, `version` 인스턴스가 79번째 줄의 구버전 상태(예: `isCompleteChampions = false`)를 유지하고 있다면 `REPLACE` 쿼리로 인해 직전에 완료 처리한 플래그들이 도로 `false`로 롤백될 위험이 존재함.

### 2.4 원인 4: 스플래시 해제 타이밍과 ViewModel StateFlow 수신 타이밍 불일치
- `HomeViewModel`에서 `refreshAppStartData.invoke()`가 끝나자마자 `_isInitComplete.emit(true)`를 발행하지만, UI StateFlow인 `_homeUiState.versionList`가 Room Flow의 최신 값을 수신하기 전에 스플래시가 해제될 경우 찰나의 순간 동안 구버전 리스트가 다이얼로그에 표시될 수 있음.

---

## 3. 상세 개선 계획

### 3.1 `core:database` 모듈
- **[수정] [VersionDao.kt](file:///C:/Users/SanDol/.gemini/antigravity/worktrees/champion/improve_app_sync_logic/core/database/src/main/java/com/sandorln/database/dao/VersionDao.kt)**
  - Room Flow와 별개로, 현재 SQLite DB에 저장된 전체 버전을 즉시 단발성으로 조회할 수 있는 suspend 함수 추가:
    ```kotlin
    @Query("SELECT * FROM VersionEntity")
    suspend fun getAllVersionEntityList(): List<VersionEntity>
    ```
  - 전체 Entity를 통째로 덮어쓰지 않고 `newChampionIdList`와 `newItemIdList` 컬럼만 안전하게 갱신하는 부분 업데이트 쿼리 추가:
    ```kotlin
    @Query("UPDATE VersionEntity SET newChampionIdList = :newChampionIdList, newItemIdList = :newItemIdList WHERE name = :versionName")
    suspend fun updateNewIdList(versionName: String, newChampionIdList: List<String>?, newItemIdList: List<String>?)
    ```

### 3.2 `core:data` 모듈
- **[수정] [VersionRepository.kt](file:///C:/Users/SanDol/.gemini/antigravity/worktrees/champion/improve_app_sync_logic/core/data/src/main/java/com/sandorln/data/repository/version/VersionRepository.kt)**
  - 신규 메서드 선언:
    ```kotlin
    suspend fun getAllVersionList(): List<Version>
    suspend fun updateNewIdList(versionName: String, newChampionIdList: List<String>?, newItemIdList: List<String>?)
    ```
- **[수정] [DefaultVersionRepository.kt](file:///C:/Users/SanDol/.gemini/antigravity/worktrees/champion/improve_app_sync_logic/core/data/src/main/java/com/sandorln/data/repository/version/DefaultVersionRepository.kt)**
  - `getAllVersionList()` 구현: `versionDao.getAllVersionEntityList()`를 호출하고 버전 내림차순(최신순)으로 정렬하여 `List<Version>` 반환.
  - `updateNewIdList()` 구현: `versionDao.updateNewIdList(versionName, newChampionIdList, newItemIdList)` 호출.

### 3.3 `core:domain` 모듈
- **[수정] [RefreshAppStartData.kt](file:///C:/Users/SanDol/.gemini/antigravity/worktrees/champion/improve_app_sync_logic/core/domain/src/main/java/com/sandorln/domain/usecase/RefreshAppStartData.kt)**
  - 1단계(데이터 다운로드 및 완료 플래그 저장) 후, 2단계에서 비동기 `StateFlow` 대신 `versionRepository.getAllVersionList()`를 직접 호출하여 **100% 최신 DB 상태**를 조회.
  - 2단계 비교 대상 선별:
    - `isCompleteChampions && isCompleteItems`이고,
    - `newChampionIdList == null || newItemIdList == null`인 버전에 대해 비교 수행.
  - 갱신 시 `updateVersionData` 대신 `updateNewIdList`를 사용하여 다른 필드의 롤백 위험 원천 차단.

### 3.4 `feature:home` 모듈
- **[수정] [HomeViewModel.kt](file:///C:/Users/SanDol/.gemini/antigravity/worktrees/champion/improve_app_sync_logic/feature/home/src/main/java/com/sandorln/home/ui/home/HomeViewModel.kt)**
  - `refreshAppStartData.invoke()` 완료 후, `_allVersionList`가 최신 데이터를 1회 이상 방출하여 `_homeUiState.versionList`가 채워진 상태를 확인한 후 `_isInitComplete.emit(true)` 하도록 타이밍 동기화 보강.

---

## 4. 기능별 커밋 계획

1. `feat : VersionDao 및 VersionRepository에 직접 조회 및 NewIdList 부분 업데이트 기능 추가`
   - `VersionDao.kt`, `VersionRepository.kt`, `DefaultVersionRepository.kt`
2. `feat : RefreshAppStartData에서 최신 DB 직접 조회 및 신규 아이디 목록 원자적 갱신 로직 개선`
   - `RefreshAppStartData.kt`
3. `feat : HomeViewModel 초기화 완료 플래그와 버전 리스트 반영 동기화 처리`
   - `HomeViewModel.kt`

---

## 5. 검증 계획

### 5.1 빌드 검증
- `./gradlew assembleDebug` 또는 `./gradlew testDebugUnitTest` 정상 통과 확인.

### 5.2 단위 테스트 및 로직 검증
- `RefreshAppStartData`에 대한 Mock 단위 테스트 작성/실행:
  - 새로운 버전 추가 시 1회차 실행만으로 `newChampionIdList`와 `newItemIdList`가 즉시 계산되어 `updateNewIdList`가 호출되는지 검증.
- `VersionDao` 쿼리 정상 동작 검증.

### 5.3 수동 동작 확인
- 앱 첫 실행 또는 신규 버전 모의 주입 시, 스플래시 종료 직후 상단 버전 선택 바를 눌렀을 때 새 챔피언/아이템과 'NEW' 뱃지가 즉시 노출되는지 확인.

---

## 6. LLM Wiki 갱신 계획

- `20_Wiki/Concepts/App_Startup_Sync_Architecture.md`: 초기 앱 구동 시 데이터 동기화 파이프라인 및 캐시 정합성 설계 문서화
- `20_Wiki/Entities/LOL_Champion_App.md`: 초기화 흐름 개선 내역 링크 반영
- `20_Wiki/Indexes.md`: 신규 개념 색인 추가
