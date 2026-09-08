# [Inbox] Riot Data Dragon 패치 노트 데이터 수집 파이프라인

- **수집 일시**: 2026-09-06
- **출처**: Riot Developer Community & LoL Patch Pipeline Notes
- **상태**: 수집 대기 (Inbox)

---

## 내용 요약
라이엇 게임즈의 롤 클라이언트는 각 패치 버전(예: 14.1, 14.2)마다 챔피언의 밸런스 변경점(공격력, 방어력, 스킬 계수 등)을 패치 노트 형태로 공개한다.

본 애플리케이션에서는 Data Dragon API의 버전 목록(`versions.json`)을 폴링하여 신규 버전 감지 시 챔피언 변경 데이터를 자동으로 로컬 DB(Room)에 업데이트하고, 사용자에게 변경된 수치 요약(Patch Note List)을 제공하는 기능을 지원한다.

스킬 변경점 파싱 시 HTML 태그 제거 및 정규표현식 기반 수치 차이 계산 알고리즘이 적용된다.
