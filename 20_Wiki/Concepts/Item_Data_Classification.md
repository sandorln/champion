# Item Data Classification (아이템 데이터 분류 및 맵 필터링 정책)

- **유형**: 도메인 비즈니스 로직 및 데이터 가공 규약 (Domain Logic & Data Transformation)
- **관련 프로젝트**: [[LOL_Champion_App]]
- **관련 개념**: [[Riot_DataDragon_API]]
- **사용 기술**: [[Room]], Kotlin Flow

---

## 1. 개요 및 배경
- [EXTRACTED] 라이엇 게임즈의 [[Riot_DataDragon_API]]는 `item.json`을 통해 게임 내 모든 아이템 정보를 제공하나, 정규 5v5 맵(소환사의 협곡) 외에도 과거 모드, 특수 이벤트, 아레나(Arena) 등 다양한 파생 데이터가 혼재되어 있다.
- [INFERRED] [[LOL_Champion_App]]은 사용자가 선택한 맵과 필터 조건에 부합하는 정제된 아이템 목록을 제공하기 위해 다단계 데이터 정제 및 맵 분류 파이프라인을 구축하였다.

---

## 2. 맵 분류 체계 (MapType Architecture)

### 2.1 맵 식별자 매핑
- [EXTRACTED] **소환사의 협곡 (Summoner's Rift)**: ID `"1"`, `"2"`, `"11"`, `"SummonersRift"`
- [EXTRACTED] **칼바람 나락 (ARAM)**: ID `"12"`, `"14"`
- [EXTRACTED] **아레나 (Arena)**: ID `"30"` (Rings of Wrath)
- [INFERRED] 정규 맵 플래그에 부합하지 않거나 특수 모드 전용 아이템은 `CLASSIC` 또는 `NONE`("그 외")으로 분류한다.

### 2.2 특수 ID 대역 격리 정책
1. **클래식 아이템 (`77xxxx`)**:
   - [EXTRACTED] ID가 `77`로 시작하는 과거 단종 아이템(예: 심연의 홀, 영혼의 갑옷 등)은 `MapType.CLASSIC`으로 분류하여 협곡/칼바람 목록에서 분리한다.
2. **특수 모드/테스트 잔재 (`66xxxx` 6자리)**:
   - [EXTRACTED] 라이엇 데이터 상 `maps["11"] = true`로 잘못 설정된 균일가(2,500G) 특수 아이템(예: `663058` 용암의 방패, `663060` 신성의 검, `667666` 징수의 총 등)은 정규 협곡 상점에 존재하지 않는다.
   - [INFERRED] `id.length > 4 && id.startsWith("66")` 조건에 해당하는 아이템은 `MapType.NONE`으로 강제 분류하여 협곡 목록 노출을 방지한다.
3. **아레나 고유 아이템 (`isArenaItem`)**:
   - [EXTRACTED] 아레나 모드 전용 프리즘/고유 완성 아이템(`44xxxx` 6자리), 전설/모루 증강 아이템(`22xxxx` 6자리), 고유 소모품/전설(`2142~2146`, `3430`, `4010~4017` 등)은 `MapType.ARENA`로 분류한다.

---

## 3. 아이템 정제 및 무결성 필터링 알고리즘

### 3.1 삭제 및 비판매 아이템 제외
- [EXTRACTED] `inStore == false` 또는 `gold.purchasable == false`인 아이템(예: 단종된 밤의 수확자 `4636`, `4637`)은 상점 목록 필터링 단계에서 완전히 제외한다.

### 3.2 동일 명칭 아이템의 표준 ID 우선 중복 제거 (Method B)
- [INFERRED] 라이엇 데이터에는 동일한 아이템 이름(예: '징수의 총')으로 4자리 표준 ID(`3036`)와 6자리 특수 ID(`667666`)가 공존하는 경우가 있다.
- [INFERRED] `groupBy { it.name }` 후 다음 우선순위에 따라 단 1개의 대표 아이템을 선별한다:
  1. 표준 4자리 이하 ID 우선 (`it.id.length <= 4`)
  2. 범용 맵 타입 우선 (`it.mapType == MapType.ALL`)
  3. 작은 ID 번호 우선 (`it.id`)

### 3.3 버전별 데이터 예외 보정
1. **오른(Ornn) 걸작 아이템 분류 (Patch 14.13.1 이후)**:
   - [EXTRACTED] 14.13.1 패치에서 오른의 걸작 아이템 시스템이 개편되어 기존 전용 ID(`7000~7029`)가 삭제되었다.
   - [INFERRED] 14.13.1 이상 버전에서는 오른 전용 아이템 분류 로직을 스킵하여 일반 아이템이 오른 아이템으로 오분류되는 현상을 방지한다.
2. **건메탈 군화 (`3172`) 신발 태그 보정 (Patch 14.10.1 이후)**:
   - [EXTRACTED] 서풍이 14.10.1 패치에서 '건메탈 군화'로 리워크되면서 신발 아이템으로 변경되었으나, 라이엇의 Data Dragon 태그에 `Boots`가 누락되었다.
   - [INFERRED] 14.10.1 이상 버전의 `3172` 아이템에는 `ItemTagType.Boots` 태그를 동적으로 주입하여 장화 분류에 정상 표시되도록 보정한다.

---

## 4. 출처 및 참고 문헌
- [EXTRACTED] `core/data/src/main/java/com/sandorln/data/util/Item.kt`
- [EXTRACTED] `core/data/src/main/java/com/sandorln/data/util/Map.kt`
- [EXTRACTED] `feature/item/src/main/java/com/sandorln/item/ui/home/ItemHomeViewModel.kt`
- [EXTRACTED] Riot Games Data Dragon Patch 14.10 ~ 16.17 `item.json`
