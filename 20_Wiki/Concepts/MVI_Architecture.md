# MVI Architecture (Model-View-Intent)

- **유형**: 아키텍처 패턴 (Architectural Pattern)
- **상위 카테고리**: 안드로이드 소프트웨어 아키텍처
- **관련 엔티티**: [[LOL_Champion_App]], [[Jetpack_Compose]]
- **관련 개념**: [[Sprite_Sheet_Optimization]]

---

## 1. 개념 정의
[EXTRACTED] MVI(Model-View-Intent)는 데이터가 단일 방향으로만 흐르도록 강제하는 단방향 데이터 흐름(Unidirectional Data Flow, UDF) 기반의 UI 아키텍처 패턴이다.

UI 레이어에서 발생한 이벤트는 **Intent(Action)**로 캡슐화되어 ViewModel에 전달되고, ViewModel은 이를 처리하여 새로운 불변 상태(**UiState**)를 생성하여 View로 방출한다.

---

## 2. 핵심 구성 요소

### 2.1 Action (Intent)
- [EXTRACTED] 사용자의 인터랙션 및 시스템 이벤트를 표현하는 불변 실드 인터페이스(Sealed Interface).
- [EXTRACTED] 예: `ChampionHomeAction.RefreshChampionData`, `ChangeChampionSearchKeyword`, `ChangeSpan` 등.
- [INFERRED] 단일 함수(`sendAction`)를 통해서만 상태 변경 의도가 수신되므로 이벤트 추적 및 디버깅이 용이하다.

### 2.2 UiState
- [EXTRACTED] 특정 시점의 화면 전체 상태를 온전히 담고 있는 불변 데이터 클래스(Immutable Data Class).
- [EXTRACTED] `StateFlow`를 통해 Compose에 전달되며, Compose 컴포넌트는 오직 이 상태를 구독하여 리컴포지션(Recomposition)을 수행한다.

### 2.3 SideEffect
- [EXTRACTED] 화면 전환, 토스트 노출, 스낵바 등 1회성(One-shot)으로 소비되어야 하는 부수 효과.
- [EXTRACTED] `SharedFlow`를 활용하여 구독 시점에 안전하게 방출 및 소비된다.

---

## 3. [[LOL_Champion_App]]에서의 적용 이점
1. **[[Jetpack_Compose]]와의 완벽한 궁합**:
   - [INFERRED] 불변 상태 객체와 단방향 흐름은 Compose의 선언형 패러다임과 정확히 일치하여 UI 상태 불일치 버그를 원천 차단한다.
2. **테스트 용이성**:
   - [INFERRED] 특정 Action을 주입했을 때 방출되는 UiState의 일치 여부만 단언(Assert)하면 되므로 단위 테스트 작성이 매우 직관적이다.

---

## 4. 출처 및 참고 문헌
- [EXTRACTED] `10_Raw_Sources/Project_Docs/LOL_Champion_Architecture_Spec.md`
- [EXTRACTED] `feature/champion/src/main/java/com/sandorln/champion/ui/home/ChampionHomeViewModel.kt`
