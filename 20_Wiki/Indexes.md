# LLM Wiki 지식 그래프 색인 및 마스터 맵 (Master Index)

이 문서는 `sandorln/champion` 프로젝트의 모든 원본 소스, 아키텍처 개념, 기술 엔티티를 연결하는 마스터 지식 맵입니다.

---

## 1. 아키텍처 및 핵심 이론 (Concepts)
- [[MVI_Architecture]]: 안드로이드 단방향 데이터 흐름(UDF), Action-UiState-SideEffect 패턴
- [[Riot_DataDragon_API]]: 라이엇 게임즈 공식 정적 CDN API 및 버전 관리 구조
- [[Sprite_Sheet_Optimization]]: 대량 아이콘 렌더링 및 네트워크 병목 해소를 위한 비트맵 슬라이싱 최적화
- [[App_Startup_Sync_Architecture]]: 초기 앱 구동 시 스플래시 화면 2단계 데이터 동기화 및 캐시 정합성 설계

---

## 2. 프로젝트 및 기술 컴포넌트 (Entities)
- [[LOL_Champion_App]]: 안드로이드 리그 오브 레전드 정보 앱 본체 및 모듈 구조
- [[Riot_Games]]: 게임 개발사 및 Data Dragon API 제공 주체
- [[Jetpack_Compose]]: Kotlin 기반 선언형 UI 렌더링 엔진
- [[Ktor]]: 코루틴 기반 비동기 HTTP 네트워크 클라이언트
- [[Room]]: 오프라인 캐싱 및 로컬 SQLite 데이터 영속화 레이어

---

## 3. 원본 보존 소스 맵 (Immutable Raw Sources)
- **프로젝트 명세서**: `10_Raw_Sources/Project_Docs/LOL_Champion_Architecture_Spec.md`
- **수집 대기 인박스**: `00_Inbox/Sample_Patch_Note_Pipeline.md`

---

## 4. 지식 거버넌스 및 신뢰도 규약
- **[EXTRACTED]**: 원본 소스 또는 코드에서 직접 발췌한 검증된 팩트
- **[INFERRED]**: 논리적 흐름 및 구조 분석을 통해 에이전트가 도출한 지식
- **[UNVERIFIED]**: 추가 검증이 필요한 지식
