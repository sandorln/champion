# Jetpack Compose

- **유형**: UI 프레임워크 (Declarative UI Toolkit)
- **제공자**: Google
- **관련 개념**: [[MVI_Architecture]], [[Sprite_Sheet_Optimization]]
- **관련 프로젝트**: [[LOL_Champion_App]]

---

## 1. 개요
[EXTRACTED] Jetpack Compose는 안드로이드의 최신 공식 선언형(Declarative) UI 툴킷으로, 기존의 XML 기반 View 시스템을 대체하고 순수 Kotlin 코드로 직관적인 반응형 UI를 구축할 수 있게 돕는다.

---

## 2. [[LOL_Champion_App]]에서의 역할
- [EXTRACTED] 앱의 모든 화면(`feature:*` 모듈)이 Jetpack Compose로 구현되어 있다.
- [INFERRED] [[MVI_Architecture]]의 단방향 데이터 흐름과 결합되어, ViewModel의 `UiState`가 변경될 때 Compose의 스마트 리컴포지션(Recomposition)을 통해 효율적으로 화면을 갱신한다.
- [EXTRACTED] 챔피언 상세 화면 및 복잡한 레이아웃 애니메이션을 위해 MotionLayout을 Compose와 결합하여 사용한다.

---

## 3. 출처 및 참고 문헌
- [EXTRACTED] `10_Raw_Sources/Project_Docs/LOL_Champion_Architecture_Spec.md`
- [EXTRACTED] Android Developers Official Compose Documentation
