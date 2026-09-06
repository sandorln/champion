# App Startup Sync Architecture (초기 데이터 동기화 아키텍처)

- **유형**: 동기화 전략 및 데이터 파이프라인 (Synchronization Pattern)
- **상위 카테고리**: 안드로이드 데이터 아키텍처
- **관련 엔티티**: [[LOL_Champion_App]], [[Room]], [[Ktor]]
- **관련 개념**: [[MVI_Architecture]], [[Riot_DataDragon_API]]

---

## 1. 개요
[EXTRACTED] `App Startup Sync Architecture`는 안드로이드 애플리케이션 시작 시 스플래시 화면(`IntroScreen`) 단계에서 라이엇 게임즈의 [[Riot_DataDragon_API]]로부터 최신 메타 데이터를 동기화하고, 로컬 [[Room]] 데이터베이스에 영속화하는 2단계 파이프라인 구조이다.

사용자가 홈 화면에 진입했을 때 즉시 최신 패치 버전 및 새롭게 추가된 챔피언과 아이템 정보를 지연 없이 확인할 수 있도록 보장한다.

---

## 2. 2단계 동기화 파이프라인 (`RefreshAppStartData`)

### 2.1 1단계: 신규 버전 데이터 수집 및 DB 적재
- [EXTRACTED] 원격 API로부터 최신 버전 목록을 가져와 로컬에 없는 신규 버전을 식별한다.
- [EXTRACTED] 초기화가 완료되지 않은 버전(`getNotInitCompleteVersionList`)들에 대해 챔피언, 아이템, 소환사 주문, 룬 데이터를 코루틴 비동기 병렬(`async`/`awaitAll`)로 다운로드하여 Room DB에 저장한다.
- [EXTRACTED] 데이터 적재 완료 후 해당 버전의 완료 플래그(`isCompleteChampions`, `isCompleteItems` 등)를 `true`로 갱신한다.

### 2.2 2단계: 버전 간 Diff 연산 및 신규 ID 원자적 부분 갱신
- [EXTRACTED] 비동기 UI `StateFlow` 대신 로컬 DB 단발성 직접 조회(`versionRepository.getAllVersionList()`)를 호출하여 최신 DB 스냅샷을 획득한다.
- [EXTRACTED] 이전 버전(`preVersion`)과의 SQLite 서브쿼리 비교를 통해 신규 챔피언 ID 목록(`newChampionIdList`)과 아이템 ID 목록(`newItemIdList`)을 계산한다.
- [EXTRACTED] 전체 Entity를 덮어쓰지 않고 대상 컬럼만 안전하게 부분 갱신(`updateNewIdList`)하여 데이터 무결성을 보장한다.

---

## 3. 캐시 일관성 및 정합성 보장 원칙
1. **단발성 직접 조회(Direct Suspend Query)**:
   - [INFERRED] UI 관찰용 Flow나 StateFlow는 비동기 스케줄링으로 인해 직전 쓰기 작업 직후 과거 캐시(Stale Data)를 반환할 수 있다. 비즈니스 로직 내부에서는 반드시 Room suspend 직접 조회를 사용하여 Race Condition을 방지한다.
2. **원자적 부분 업데이트(Partial Atomic Update)**:
   - [INFERRED] Room `@Insert(onConflict = REPLACE)`로 엔티티를 통째로 덮어쓰면 구버전 객체의 플래그로 인해 롤백이 일어날 수 있으므로, 변경된 컬럼만 `@Query("UPDATE ...")`로 갱신한다.
3. **UI 방출 동기화 후 스플래시 해제**:
   - [INFERRED] DB 작업 완료 후 `_allVersionList` Flow가 최신 데이터를 1회 이상 정상 방출한 것을 확인한 뒤 스플래시 플래그(`isInitComplete`)를 해제하여 홈 진입 직후의 UI 깜빡임을 방지한다.

---

## 4. 출처 및 참고 문헌
- [EXTRACTED] `core/domain/src/main/java/com/sandorln/domain/usecase/RefreshAppStartData.kt`
- [EXTRACTED] `core/data/src/main/java/com/sandorln/data/repository/version/DefaultVersionRepository.kt`
- [EXTRACTED] `core/database/src/main/java/com/sandorln/database/dao/VersionDao.kt`
- [EXTRACTED] `feature/home/src/main/java/com/sandorln/home/ui/home/HomeViewModel.kt`