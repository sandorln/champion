# Riot Patch Note Crawler (패치노트 웹 크롤러 아키텍처)

## 1. 개요
`sandorln/champion` 앱에서 라이엇 게임즈 공식 웹사이트의 패치노트 페이지를 크롤링하여 챔피언, 아이템, 소환사 주문의 밸런스 변경점을 추출하고 앱 내 카드 및 탭 인터페이스로 제공하는 시스템입니다.

- **관련 엔티티**: [[LOL_Champion_App]], [[Riot_Games]]
- **색인**: [[Indexes]]

---

## 2. 웹 구조 및 Next.js SSR 데이터 추출 [EXTRACTED]

### 2.1 Next.js SSR 전환 배경
라이엇 공식 패치노트 웹페이지(`https://www.leagueoflegends.com/ko-kr/news/game-updates/...`)는 클라이언트 사이드 / SSR 하이브리드 Next.js 아키텍처로 마이그레이션되었습니다. 이에 따라 기존의 일반 DOM 셀렉터(`#patch-notes-container`)는 초기 HTML 응답에 직접 노출되지 않고, `<script id="__NEXT_DATA__" type="application/json">` 내부에 인라인 JSON으로 캡슐화되어 전달됩니다.

### 2.2 RichText 추출 파이프라인
1. **Jsoup 응답 파싱**: 초기 HTML에서 `script#__NEXT_DATA__` 태그를 탐색.
2. **JSON 순회 및 언이스케이프**: `kotlinx.serialization.json.Json`을 통해 AST 트리(`JsonElement`)를 순회하며 `richText.body`에 담긴 인라인 HTML 문자열들을 수집.
3. **가상 DOM 생성**: 추출된 HTML 조각들을 `Jsoup.parse()`하여 온전한 DOM 트리(`Element`)를 복원한 뒤 변경점 파싱을 진행.

---

## 3. URL 생성 규칙 및 Fallback 메커니즘 [EXTRACTED]

### 3.1 시즌별 URL 규격 변화
- **시즌 16+ (DDragon 16.x)**: `league-of-legends-patch-{major+10}-{minor}-notes/` 형식이 표준.
- **시즌 15 (DDragon 15.x)**: 
  - 15.1, 15.2: `patch-25-s1-{minor}-notes/`
  - 15.3: `patch-2025-s1-3-notes/`
  - 그 외: `league-of-legends-patch-25-{minor}-notes/` 및 `patch-25-{minor}-notes/`
- **시즌 14 이하**: `patch-{major}-{minor}-notes/`

### 3.2 Dynamic Fallback 전략
라이엇 웹사이트의 기사 배포 시점에 따라 `league-of-legends-patch-` 또는 `patch-` 접두사가 유동적으로 사용될 수 있으므로, `getPatchNoteUrlCandidates()`는 복수의 후보 URL 목록을 생성하고, `fetchPatchNoteList()`는 유효한 200 응답과 비어있지 않은 데이터를 획득할 때까지 순차적으로 Fallback 재시도를 수행합니다.

---

## 4. 소환사의 협곡 격리 및 모드 필터링 정책 [EXTRACTED]

### 4.1 필터링 배경
패치노트 문서 내에는 소환사의 협곡 외에도 **아레나(Arena)**, **무작위 총력전(ARAM)**, **버그 수정**, **스킨** 등의 섹션이 포함되어 있으며, 아레나 하위에도 동일한 이름의 `챔피언` 헤더가 존재합니다.

### 4.2 필터링 규칙
- 제외 모드 키워드(`아레나`, `무작위 총력전`, `칼바람`, `클래식`, `버그 수정`, `앞으로 나올`, `스킨`) 헤더를 감지하면 즉시 `isTargetSection = false` 및 `isOtherMode = true`로 전환.
- 소환사의 협곡 밸런스 변경점만 온전하게 파싱하여 앱 UI에 제공.

---

## 5. UI 멀티 탭 구성 [EXTRACTED]
`ChampionPatchNoteListScreen`은 상단 탭을 통해 세 가지 영역을 전환 지원합니다:
1. **챔피언**: 챔피언별 스킬 계수, 기본 능력치 변경점
2. **아이템**: 아이템 가격, 능력치, 조합식 변경점
3. **소환사 주문**: 점멸, 유체화, 강타 등 소환사 주문 밸런스 변경점
