# LOL CHAMPION 프로젝트 아키텍처 및 기술 명세서

- **문서 식별자**: SPEC-LOLCHAMPION-001
- **추출 일시**: 2026-09-06
- **출처**: `sandorln/champion` 로컬 코드베이스 (`app`, `core`, `feature`)
- **문서 상태**: Immutable Raw Source (불변 원본)

---

## 1. 프로젝트 개요
`LOL CHAMPION`은 라이엇 게임즈(Riot Games)의 Data Dragon API를 활용하여 리그 오브 레전드(League of Legends)의 최신 및 과거 버전별 챔피언, 아이템, 룬, 소환사 주문 정보를 조회하고 인게임 정보를 제공하는 안드로이드 애플리케이션이다.

- Google Play Store 패키지명: `com.sandorln.champion`
- 개발 언어: Kotlin
- UI 프레임워크: Jetpack Compose
- 아키텍처 패턴: MVI (Model-View-Intent / Action-State-SideEffect)
- 타겟/최소 SDK: Min 26 / Target 34

---

## 2. 모듈 아키텍처 구성
프로젝트는 관심사 분리와 빌드 캐싱 최적화를 위해 멀티 모듈 구조를 채택하고 있다.

1. **`:app`**:
   - 애플리케이션 진입점 및 전체 네비게이션 호스트
   - Dagger-Hilt 의존성 주입 루트

2. **`:core` 계층**:
   - `:core:model`: 도메인 전반에서 공통 사용되는 순수 데이터 모델 (Champion, Item, Rune, Spell, Sprite 등)
   - `:core:network`: Ktor 기반의 Riot Data Dragon HTTP 비동기 통신 클라이언트
   - `:core:database`: Room 기반 로컬 SQLite 데이터베이스 (오프라인 캐싱 지원)
   - `:core:datastore`: 사용자 설정 및 최신 버전 정보 영속화
   - `:core:design`: 공통 디자인 시스템 컴포넌트, 테마, 모션 레이아웃
   - `:core:domain`: 유즈케이스(UseCase) 및 리포지토리 인터페이스 정의
   - `:core:data`: 리포지토리 구현체 및 로컬/원격 데이터 소스 조율

3. **`:feature` 계층**:
   - `:feature:home`: 메인 대시보드 화면
   - `:feature:champion`: 챔피언 리스트, 상세 정보, 스킨 뷰어, 패치노트 연동
   - `:feature:item`: 아이템 도감 및 조합 트리
   - `:feature:rune`: 룬 빌드 및 상세 정보
   - `:feature:spell`: 소환사 주문
   - `:feature:game`: 미니 게임 및 퀴즈 기능
   - `:feature:setting`: 앱 버전, 데이터 동기화 관리 화면

---

## 3. 핵심 기술 구현 패턴

### 3.1 MVI (Model-View-Intent)
화면별 ViewModel(예: `ChampionHomeViewModel`)은 단방향 데이터 흐름을 철저히 준수한다:
- **Action (Intent)**: 사용자 입력 또는 화면 이벤트를 `sendAction(ChampionHomeAction)` 형태로 단일 진입점 전달
- **UiState**: 불변 상태 객체(`ChampionHomeUiState`)를 `StateFlow`로 노출하여 Compose UI에 발행
- **SideEffect**: 1회성 이벤트(토스트, 화면 이동 등)를 `SharedFlow`로 분리 발행

### 3.2 대용량 에셋 최적화: 스프라이트 시트 (Sprite Sheet) 파이프라인
- 라이엇 Data Dragon API는 수백 개의 챔피언/아이템 개별 이미지를 매번 요청하지 않고, 묶음 형태인 Sprite 이미지로 제공한다.
- 앱은 스프라이트 시트 비트맵을 다운로드하여 로컬 캐시하고, 인게임 좌표(`x`, `y`, `w`, `h`)를 계산하여 필요한 영역만 슬라이싱하여 렌더링함으로써 메모리 사용량과 네트워크 대역폭을 90% 이상 절감한다.
