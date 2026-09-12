# [계획서] 챔피언 상세 화면 스킨 목록 및 스킨 이미지 URL 정상화

- **작성 일자**: 2026-09-12
- **기준 브랜치**: `develop` (최신 커밋: `ca6a840`)
- **작업 브랜치**: `feat/fix_champion_skin_list`
- **문서 상태**: 검토 대기 (`[구현]` 승인 전 구현 금지)

---

## 1. 개발 목표 및 배경

### 1.1 배경 및 발생 이슈
- 챔피언 상세 화면의 스킨 영역에서 스킨 목록이 정상적으로 출력되지 않고 투명하게 비어있는 현상이 발생함.
- 최신 버전(16.17.1 등)에서 라이엇 DataDragon API의 스킨 데이터 구조와 이미지 URL 규격을 확인하고 이를 완벽히 연동해야 함.

### 1.2 핵심 원인 분석 결과
1. **Network DTO 타입 불일치로 인한 역직렬화 실패 (`SerializationException`)**:
   - 라이엇 DataDragon 챔피언 상세 API (`/cdn/{version}/data/ko_KR/champion/{championName}.json`)의 `skins` 배열 내 `num` 필드는 **정수형 (`Int`)** (예: `{"id": "266000", "num": 0, "name": "default"}`)입니다.
   - 하지만 [NetworkChampionSkin.kt](file:///C:/Users/SanDol/.gemini/antigravity/worktrees/champion/analyze_game_data_features/core/network/src/main/java/com/sandorln/network/model/champion/NetworkChampionSkin.kt)에서는 `val num: String = ""`로 선언되어 있어 `kotlinx.serialization`이 역직렬화 중 파싱 에러를 발생시켰습니다.
   - [DefaultChampionRepository.kt](file:///C:/Users/SanDol/.gemini/antigravity/worktrees/champion/analyze_game_data_features/core/data/src/main/java/com/sandorln/data/repository/champion/DefaultChampionRepository.kt)의 `getChampionDetail` 메서드 내부 `runCatching` 블록에서 해당 예외가 조용히 삼켜져, 스킨 정보가 완전히 빈 리스트(`emptyList()`)인 기본 로컬 DB 엔티티가 반환되었습니다.
   - 그 결과 [ChampionSkinsView.kt](file:///C:/Users/SanDol/.gemini/antigravity/worktrees/champion/analyze_game_data_features/feature/champion/src/main/java/com/sandorln/champion/ui/detail/ChampionSkinsView.kt)의 `LazyColumn` 항목 수가 0이 되고, 좌측 그라데이션 오버레이만 남아 투명하게 비어 보였습니다.

2. **크로마(Chroma) 스킨의 스플래시 이미지 미제공 (403 Forbidden)**:
   - 최신 DataDragon에서는 스킨 목록에 크로마가 함께 포함되며, 크로마 항목에는 `"parentSkin": 2`와 같이 `parentSkin` 속성이 존재합니다.
   - 라이엇 DataDragon CDN은 크로마 번호(예: `Aatrox_4.jpg`)에 대한 스플래시/로딩 이미지를 제공하지 않고 **403 Forbidden** 에러를 반환합니다.
   - 스플래시 이미지는 `parentSkin == null`인 고유 스킨(예: `Aatrox_0.jpg`, `Aatrox_1.jpg`, `Aatrox_2.jpg`, `Aatrox_7.jpg` 등)만 200 OK로 정상 응답합니다.
   - 따라서 스킨 갤러리에서는 `parentSkin == null`인 정규 스킨만 필터링하여 보여주거나, 부모 스킨 번호로 안전하게 매핑해야 합니다.

3. **버전 기준 (`develop` 최신 기준 16.17.1)**:
   - `develop` 브랜치에는 이미 최신 `16.17.1` 버전의 `lol-champion.db` 및 LFS 설정이 반영되어 있으므로, `origin/develop` 최신 커밋(`ca6a840`)을 베이스로 새 브랜치를 생성하여 최신 16.17.1 버전 기준으로 동작을 검증합니다.

---

## 2. 상세 구현 계획

### 2.1 네트워크 계층 (`core:network`)
#### [MODIFY] [NetworkChampionSkin.kt](file:///C:/Users/SanDol/.gemini/antigravity/worktrees/champion/analyze_game_data_features/core/network/src/main/java/com/sandorln/network/model/champion/NetworkChampionSkin.kt)
- `num` 속성 타입을 `Int = 0`으로 수정 (또는 Int/String 유연 파싱).
- `parentSkin: Int? = null` 속성 추가 (크로마 여부 식별용).
```kotlin
@Serializable
data class NetworkChampionSkin(
    val id: String = "",
    val name: String = "",
    val num: Int = 0,
    var chromas: Boolean = false,
    val parentSkin: Int? = null
)
```

### 2.2 도메인 및 데이터 계층 (`core:model`, `core:data`)
#### [MODIFY] [ChampionSkin.kt](file:///C:/Users/SanDol/.gemini/antigravity/worktrees/champion/analyze_game_data_features/core/model/src/main/java/com/sandorln/model/data/champion/ChampionSkin.kt)
- `num: String = "0"`, `parentSkin: Int? = null` 필드 지원.
- 스플래시 이미지가 존재하는 고유 스킨인지 판별하는 프로퍼티 (`val hasSplashImage: Boolean get() = parentSkin == null`) 추가.

#### [MODIFY] [Champion.kt](file:///C:/Users/SanDol/.gemini/antigravity/worktrees/champion/analyze_game_data_features/core/data/src/main/java/com/sandorln/data/util/Champion.kt)
- `NetworkChampionSkin.asData()` 매핑 함수 업데이트:
```kotlin
fun NetworkChampionSkin.asData(): ChampionSkin = ChampionSkin(
    id = id,
    name = name,
    num = num.toString(),
    chromas = chromas,
    parentSkin = parentSkin
)
```
- `NetworkChampionDetail.asData()`에서 스킨 목록 매핑 시 스플래시 이미지가 없는 크로마를 제외하고 순수 스킨만 필터링하거나 안전하게 정제:
```kotlin
skins = skins.map(NetworkChampionSkin::asData).filter { it.parentSkin == null }
```

### 2.3 UI 컴포넌트 계층 (`feature:champion`, `core:design`)
#### [MODIFY] [ChampionSkinsView.kt](file:///C:/Users/SanDol/.gemini/antigravity/worktrees/champion/analyze_game_data_features/feature/champion/src/main/java/com/sandorln/champion/ui/detail/ChampionSkinsView.kt)
- 스킨 목록이 비어있을 경우에 대한 안전한 Fallback 처리 (기본 스킨 1개 기본 노출).
- 스킨 썸네일 클릭 시 부드러운 스플래시 전환 및 현재 선택된 스킨명 표시 로직 검증.

#### [MODIFY] [BaseChampionImage.kt](file:///C:/Users/SanDol/.gemini/antigravity/worktrees/champion/analyze_game_data_features/core/design/src/main/java/com/sandorln/design/component/BaseChampionImage.kt)
- Glide 이미지 로딩 시 플레이스홀더 및 에러 처리 (`placeholder`, `error`) 보강.

---

## 3. 기능별 커밋 계획

1. `fix : NetworkChampionSkin num 정수형 역직렬화 및 parentSkin 필드 추가`
   - DataDragon 챔피언 상세 JSON 파싱 에러 해결
2. `feat : ChampionSkin 모델 parentSkin 연동 및 크로마 필터링 매핑`
   - 스플래시 이미지가 존재하는 실제 스킨만 선별하여 도메인 모델 전달
3. `fix : 챔피언 상세 스킨 뷰(ChampionSkinsView) 목록 렌더링 및 Fallback 안전성 개선`
   - 스킨 목록 선택 및 이미지 전환 UI 검증
4. `test : 최신 버전(16.17.1) 챔피언 상세 스킨 데이터 파싱 및 네트워크 단위 테스트`
   - ConnectTest 및 단위 테스트 작성/통과
5. `docs : 챔피언 스킨 데이터 모델 및 DataDragon 스플래시 규격 위키 갱신`
   - LLM Wiki 동기화

---

## 4. 검증 계획

### 4.1 자동화 단위 테스트
- `ConnectTest.kt`에 최신 버전(16.17.1) 챔피언 상세(Aatrox, Ahri 등) 스킨 목록 파싱 검증 테스트 추가.
- `./gradlew :core:network:testDebugUnitTest`, `./gradlew :core:data:testDebugUnitTest`, `./gradlew :feature:champion:testDebugUnitTest` 실행.

### 4.2 에뮬레이터 수동 검증
- `develop` 최신 기반으로 디버그 빌드 (`./gradlew :app:assembleDebug`).
- 에뮬레이터에서 앱 실행 후:
  1. 상단 버전이 최신 버전(`16.17.1`)으로 정상 표시되는지 확인.
  2. 아트록스 / 아리 등 챔피언 상세 화면 진입.
  3. "스킨" 섹션 좌측에 스킨 썸네일 목록(기본, 정의의 아트록스, 메카 아트록스 등)이 선명하게 나타나는지 확인.
  4. 썸네일 클릭 시 메인 스플래시 이미지와 하단 스킨명이 투명해지지 않고 정상 변경되는지 스크린샷 캡처 검증.

---

## 5. LLM Wiki 갱신 계획
- `20_Wiki/Concepts/Riot_DataDragon_API.md`: 챔피언 스킨 API의 `num` 정수형 규격 및 크로마(`parentSkin`) 스플래시 URL 정책 정리.
- `20_Wiki/Indexes.md` 및 `lint_wiki.ps1` 무결성 검증.
