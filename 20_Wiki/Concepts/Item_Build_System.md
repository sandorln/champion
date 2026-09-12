# Item Build System (아이템 빌드 시스템 아키텍처)

- **유형**: 도메인 비즈니스 로직 및 사용자 데이터 관리 아키텍처 (Domain Logic & Architecture Spec)
- **관련 프로젝트**: [[LOL_Champion_App]]
- **관련 개념**: [[MVI_Architecture]], [[Item_Data_Classification]]
- **사용 기술**: [[Room]], [[Jetpack_Compose]], Hilt, Kotlin Coroutines/Flow

---

## 1. 개요 및 배경
- [INFERRED] 사용자가 특정 패치 버전 및 포지션별로 맞춤형 아이템 세트를 생성, 편집, 보관할 수 있도록 지원하는 **아이템 빌더 시스템(Item Build System)**이다.
- [EXTRACTED] 홈 화면 아이템 탭(`ItemHomeScreen`)의 상단 헤더 필터 버튼 인접 위치에 연필 모양의 빌더 진입 아이콘(`ic_pencil`)을 제공하여 접근성을 확보하였다.
- [INFERRED] 사용자 작성 데이터는 게임 마스터 데이터(Data Dragon) 업데이트 및 캐시 갱신 주기와 독립적으로 안전하게 영속화되어야 하므로 별도의 전용 데이터베이스 및 생명주기를 부여하였다.

---

## 2. 데이터 영속화 및 격리 설계 (Database Isolation)

### 2.1 마스터 DB와 사용자 DB의 물리적 격리
- [INFERRED] 앱의 게임 데이터베이스(`lol-champion.db`)는 에셋 사전 패키징 및 마스터 데이터 갱신 시 재생성되거나 마이그레이션 대상이 될 수 있다.
- [INFERRED] 사용자가 생성한 빌드 데이터의 손실 및 충돌을 원천 차단하기 위해 독립된 `ItemBuildDatabase`(`item-build.db`)를 별도로 분리 구축하였다.
  - 마스터 DB: `ChampionRoomDatabase` (`lol-champion.db`) - 챔피언/아이템/버전/게임 마스터 데이터
  - 사용자 빌드 DB: `ItemBuildDatabase` (`item-build.db`) - 사용자 생성 아이템 빌드 전용

### 2.2 엔티티 및 도메인 모델 매핑
- [EXTRACTED] **Room Entity (`ItemBuildEntity`)**:
  - `id`: 자동 증가 Primary Key (`Long`, `autoGenerate = true`)
  - `version`: 빌드가 귀속된 롤 패치 버전 (`String`)
  - `title`: 빌드 명칭 (`String`)
  - `positionsJson`: 포지션 리스트 JSON 직렬화 문자열 (`String`, 예: `["TOP","JUNGLE"]`)
  - `itemIdsJson`: 6개 슬롯 아이템 ID 리스트 JSON 직렬화 문자열 (`String`, 예: `["3031","3036",...]`)
  - `createdAt`: 생성 일시 타임스탬프 (`Long`)
  - `updatedAt`: 최종 수정 일시 타임스탬프 (`Long`)
- [EXTRACTED] **Domain Model (`ItemBuild`)**:
  - 외부 프레임워크나 라이브러리(Room 등) 종속성 없는 순수 Kotlin 데이터 모델.
  - `id`, `version`, `title`, `positions: List<PositionType>`, `itemIds: List<String>`, `createdAt`, `updatedAt` 속성 보유.

---

## 3. 버전별 용량 제한 거버넌스 (Capacity Governance)

### 3.1 버전당 최대 10개 빌드 제한 규칙
- [EXTRACTED] 사용자의 무분별한 데이터 증식 방지 및 UI 탐색 효율성을 위해 **동일 패치 버전당 최대 10개**의 빌드만 등록할 수 있도록 강제한다.
- [INFERRED] 데이터 정합성 보장을 위해 UI 레이어뿐만 아니라 비즈니스 로직의 중심인 `SaveItemBuild` UseCase 내에서 단일 진실 공급원(SSOT) 원칙으로 한도를 검증한다:
  1. 신규 빌드 생성(`itemBuild.id == 0L`):
     - `getItemBuildListByVersion(version)`으로 현재 등록된 빌드 개수를 조회.
     - `currentCount >= 10`인 경우 `IllegalStateException("해당 버전의 아이템 빌드는 최대 10개까지 저장할 수 있습니다.")` 예외를 즉시 방출.
  2. 기존 빌드 수정(`itemBuild.id > 0L`):
     - 기저장된 아이템의 내용 변경이므로 개수 제한 검증을 생략하고 즉시 업데이트.
- [EXTRACTED] 빌드 목록 화면(`ItemBuilderListScreen`) 상단 탑바에서 `아이템 빌드 (N/10)` 형식으로 실시간 잔여 슬롯을 표기하며, 10개 도달 시 신규 추가 버튼 동작을 차단하고 안내 스낵바를 표시한다.

---

## 4. UI 아키텍처 및 화면 흐름 (MVI Flow)

### 4.1 화면 계층 구조
1. **`ItemHomeScreen`**:
   - `ItemStickyHeader`에 `ic_pencil` 아이콘 버튼 배치 -> 클릭 시 `moveToItemBuilderList(selectedVersion)` 트리거.
2. **`ItemBuilderListScreen`**:
   - 탑바: 뒤로가기, 타이틀(`아이템 빌드 (N/10)`), 새 빌드 추가(`+`) 아이콘.
   - 중앙 리스트: `ItemBuilderCard` 목록 렌더링.
     - `ItemBuilderCard`: 가로형 카드, 모서리 라운드(`Radius04`), 좌측 정렬 타이틀, 포지션 배지(`ChampionTag`), 가로 아이템 아이콘 행, 우측 더보기 아이콘(`ic_menu_vertical`) 및 수정/삭제 팝업 메뉴.
   - 빈 상태(Empty State): 등록된 빌드가 없을 시 안내 문구 및 `[새 빌드 추가]` 버튼 제공.
3. **`ItemBuilderEditScreen`**:
   - 탑바: 좌측 취소/뒤로가기, 우측 `[저장]` 버튼 (제목 미입력, 포지션 0개, 아이템 0개 시 비활성화).
   - MotionLayout 상단 인터랙션 영역:
     - 확장(Expanded): 타이틀 입력창(`LolTextField`), 6대 포지션 선택 칩(탱커, 암살자, 전사, 마법사, 원거리 딜러, 서포터).
     - 축소(Collapsed): 스크롤 시 타이틀 입력 필드가 상단 탑바로 통합 렌더링되며 소프트 키보드가 자동 수납(`LocalFocusManager.clearFocus()`, `LocalSoftwareKeyboardController.hide()`).
   - 고정 슬롯 영역 (Fixed 6 Slots):
     - 항상 6개의 슬롯 프레임 유지.
     - 담긴 아이템이 있을 시 아이콘 렌더링, 클릭 시 해당 슬롯 아이템 제거.
   - 하단 카탈로그 영역:
     - 실시간 검색창 및 카테고리별 아이템 리스트 (기존 `baseItemList` 재사용).
     - 아이템 클릭 시 `ItemDetailDialog(isBuilderMode = true)` 노출 -> 하단 `[추가하기]` 버튼 클릭 시 빈 슬롯에 자동 적재.

---

## 5. 지식 상호 연결 (Bidirectional Cross-Linking)
- [[Item_Data_Classification]]: 아이템 빌더에서 선택되는 정제된 아이템 데이터 카탈로그 소스.
- [[MVI_Architecture]]: `ItemBuilderListViewModel`, `ItemBuilderEditViewModel`의 단방향 상태 머신 구현 규약.
- [[LOL_Champion_App]]: 해당 시스템이 탑재된 애플리케이션 모듈 구조.
