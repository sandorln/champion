# Game Mode Architecture (미니게임 아키텍처)

- **유형**: 시스템 아키텍처 및 게임 엔진 구조 (Architecture / Game Engine)
- **상위 프로젝트**: [[LOL_Champion_App]]
- **관련 개념**: [[MVI_Architecture]], [[Room]], [[Jetpack_Compose]]

---

## 1. 개요
[EXTRACTED] `LOL CHAMPION` 앱 내의 `:feature:game` 모듈은 리그 오브 레전드의 로컬 캐시 데이터([[Room]])를 활용하여 사용자가 즐길 수 있는 미니게임 엔진을 제공한다. 초기 단일 초성 퀴즈 체제에서 다중 게임을 수용하는 **게임 허브(Game Hub)** 구조로 확장되었다.

---

## 2. 지원 게임 모드

### 2.1 아이템 초성 퀴즈 (Initial Quiz)
- [EXTRACTED] 버전별 유효 아이템 풀에서 무작위 아이템을 선정한 뒤, 한글 초성을 추출하여 제한 시간(60초) 내에 타이핑으로 맞추는 스피드 퀴즈.
- [EXTRACTED] 랭킹 서버 및 로컬 최고 점수 연동.

### 2.2 아이템 조합식 퀴즈 (Item Recipe Quiz)
- [EXTRACTED] 목표 완성 아이템을 출제하고, 해당 아이템을 만들기 위해 필요한 **최소 기초 재료(Leaf Items)**와 **필요 수량(Quantity)**을 맞추는 조합 퍼즐 퀴즈.
- **재귀적 리프 아이템 분해 알고리즘**:
  - `ItemEntity.from` 조합식을 재귀적으로 추적하여 중간 서사급 조합 아이템을 배제하고, 더 이상 하위 재료가 없는(`from.isEmpty()`) 최소 기초 재료만 추출.
  - 각 기초 재료별 필요 수량을 매핑(`Map<ItemData, Int>`)하여 출제.
- **MVI 상태 기반 조합 장바구니**:
  - `ItemRecipeQuizUiState`를 통해 후보 재료 탭 시 수량 증가, 장바구니 슬롯 탭 시 수량 감소 및 조합 검증 실행.

---

## 3. MVI 상태 머신 및 채점 체계
- [EXTRACTED] 모든 게임 화면은 [[MVI_Architecture]]에 따라 `UiState`, `Action`, 비동기 타이머 코루틴 Job으로 생명주기가 관리된다.
- **체인 콤보 보너스 (ChainType)**:
  - 응답 속도에 따라 `GREAT`(3초 이내, 5배), `GOOD`(5초 이내, 3배), `NICE`(7초 이내, 2배), `NORMAL`(기본 500점) 점수를 부여.
  - 게임 종료 시 남은 시간 보너스와 정답률을 곱산하여 최종 점수를 산출.

---

## 4. 출처 및 참고 문헌
- [EXTRACTED] `docs/plans/2026-09-06_game_feature_expansion.md`
- [EXTRACTED] `feature/game/src/main/java/com/sandorln/game/ui/recipequiz/ItemRecipeQuizViewModel.kt`
- [EXTRACTED] `core/domain/src/main/java/com/sandorln/domain/usecase/item/GetItemRecipeQuizRoundList.kt`
