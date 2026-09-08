# Ktor

- **유형**: 비동기 HTTP 네트워크 라이브러리 (HTTP Client Framework)
- **개발사**: JetBrains
- **관련 개념**: [[Riot_DataDragon_API]]
- **관련 프로젝트**: [[LOL_Champion_App]]

---

## 1. 개요
[EXTRACTED] Ktor는 JetBrains에서 개발한 코루틴(Coroutines) 기반의 멀티플랫폼 비동기 HTTP 클라이언트 및 서버 프레임워크이다.

---

## 2. [[LOL_Champion_App]]에서의 역할
- [EXTRACTED] `core:network` 모듈의 메인 HTTP 통신 엔진으로 사용된다.
- [EXTRACTED] [[Riot_DataDragon_API]]의 대용량 JSON 데이터(버전, 챔피언, 아이템) 및 패치노트 엔드포인트와 통신한다.
- [INFERRED] OkHttp/Retrofit 대비 경량화된 Kotlin 순수 비동기 파이프라인을 구축하여 네트워크 성능을 극대화한다.

---

## 3. 출처 및 참고 문헌
- [EXTRACTED] `10_Raw_Sources/Project_Docs/LOL_Champion_Architecture_Spec.md`
- [EXTRACTED] `core/network/build.gradle.kts`
