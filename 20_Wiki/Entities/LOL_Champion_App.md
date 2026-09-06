# LOL CHAMPION App

- **유형**: 안드로이드 애플리케이션 프로젝트 (Application Project)
- **개발자/저자**: sandorln
- **관련 개념**: [[MVI_Architecture]], [[Riot_DataDragon_API]], [[Sprite_Sheet_Optimization]]
- **기술 스택/의존성**: [[Jetpack_Compose]], [[Ktor]], [[Room]], [[Riot_Games]]

---

## 1. 개요
[EXTRACTED] `LOL CHAMPION`은 안드로이드 환경에서 리그 오브 레전드의 과거 및 최신 버전별 챔피언, 아이템, 룬, 소환사 주문 정보를 손쉽게 검색하고 비교할 수 있는 앱이다.
- 패키지명: `com.sandorln.champion`
- 구글 플레이 스토어 배포 중

---

## 2. 모듈 구조
[EXTRACTED] 프로젝트는 독립된 책임과 빌드 속도 향상을 위해 멀티 모듈로 설계되었다:
- **`:app`**: 통합 엔트리포인트 및 Hilt 의존성 주입 구성
- **`:core:model`**: 순수 Kotlin 데이터 모델
- **`:core:network`**: [[Ktor]] 기반의 비동기 네트워크 통신 모듈
- **`:core:database`**: [[Room]] 기반 SQLite 로컬 캐시 레이어
- **`:core:domain`**: 클린 아키텍처 원칙에 따른 UseCase 모듈
- **`:core:design`**: [[Jetpack_Compose]] 공통 컴포넌트 및 테마
- **`:feature:*`**: `champion`, `item`, `rune`, `spell`, `game`, `home`, `setting` 기능 모듈

---

## 3. 핵심 아키텍처 원칙
- [EXTRACTED] 화면 레벨의 상태 관리는 [[MVI_Architecture]]를 전면 적용하여 단방향 데이터 흐름을 유지한다.
- [EXTRACTED] 데이터 통신은 [[Riot_Games]]의 [[Riot_DataDragon_API]]를 사용하며, 대량 에셋은 [[Sprite_Sheet_Optimization]]을 통해 처리한다.

---

## 4. 출처 및 참고 문헌
- [EXTRACTED] `10_Raw_Sources/Project_Docs/LOL_Champion_Architecture_Spec.md`
- [EXTRACTED] `README.md`
