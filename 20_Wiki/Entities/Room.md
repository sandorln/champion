# Room

- **유형**: 로컬 데이터베이스 ORM 라이브러리 (SQLite Persistence Library)
- **제공자**: Google (Android Jetpack)
- **관련 개념**: [[Riot_DataDragon_API]]
- **관련 프로젝트**: [[LOL_Champion_App]]

---

## 1. 개요
[EXTRACTED] Room은 SQLite 위에 추상화 레이어를 제공하여 데이터베이스 접근 시 원활한 SQLite 쿼리와 컴파일 타임 유효성 검사를 지원하는 안드로이드 공식 영속성 라이브러리이다.

---

## 2. [[LOL_Champion_App]]에서의 역할
- [EXTRACTED] `core:database` 모듈에 위치하며, 네트워크에서 다운로드한 챔피언 요약, 상세, 스킬, 아이템, 패치노트 정보를 로컬 테이블에 영속화한다.
- [INFERRED] 앱이 오프라인 상태이거나 네트워크 연결이 불안정할 때도 사용자가 끊김 없이 챔피언 데이터를 조회할 수 있도록 오프라인 우선(Offline-First) 캐시 레이어로 작동한다.
- [EXTRACTED] Flow 기반 Reactive Query를 지원하여 데이터베이스 데이터가 갱신되면 UI가 자동으로 업데이트된다.

---

## 3. 출처 및 참고 문헌
- [EXTRACTED] `10_Raw_Sources/Project_Docs/LOL_Champion_Architecture_Spec.md`
- [EXTRACTED] `core/database/build.gradle.kts`
