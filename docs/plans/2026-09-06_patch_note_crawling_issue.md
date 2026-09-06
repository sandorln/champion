# [계획서] 패치노트 크롤링 및 파싱 오류 해결

- **작성 일자**: 2026-09-06
- **기준 브랜치**: develop (`b210a4c` 최신 커밋)
- **작업 브랜치**: `feat/patch_note_crawling_fix`
- **문서 상태**: 검토 완료 및 최종 대기 (사용자의 **[구현]** 승인 전까지 코드 수정 금지)

---

## 1. 개발 목표 및 배경

### 1.1 현상 및 문제점
현재 리그 오브 레전드 패치노트 크롤링 시 다음과 같은 심각한 오류가 발생하여 아이템, 캐릭터, 주문 등의 업데이트 사항이 전혀 표기되지 않는 문제가 발생하고 있습니다:
1. **URL 생성 불일치로 인한 404 발생**:
   - 최신 시즌(시즌 26, DDragon 16.x 등)의 라이엇 공식 URL이 기존 `patch-26-17-notes/`가 아닌 `league-of-legends-patch-26-17-notes/` 형태로 변경되어 404 Not Found 에러가 발생합니다.
2. **Next.js SSR 마이그레이션으로 인한 파싱 불가 (핵심 원인)**:
   - 라이엇 홈페이지가 Next.js 기반으로 개편되면서 본문 내용이 정적 DOM이 아닌 `<script id="__NEXT_DATA__">` 태그 내부의 JSON 문자열(`page.blades[...].richText.body`)에 인코딩되어 전달됩니다.
   - 기존의 `Jsoup.selectFirst("#patch-notes-container")`가 DOM에서 컨테이너를 찾지 못하고 `null`을 반환하여, 모든 크롤링 결과가 빈 리스트(`emptyList()`)로 처리되고 있습니다.
3. **소환사 주문(Spell) 미지원**:
   - `NetworkPatchNoteType`에 `Champion`, `Item`, `Rune`만 정의되어 있고 소환사 주문(`Spell`)이 누락되어 있습니다.

### 1.2 사용자 피드백 및 설계 확정 사항
- **소환사 주문 UI 노출**: 기존 패치노트 화면([`ChampionPatchNoteScreens.kt`](file:///C:/Users/SanDol/.gemini/antigravity/worktrees/champion/build_llm_wiki_system/feature/champion/src/main/java/com/sandorln/champion/ui/patch/ChampionPatchNoteScreens.kt)) 상단 탭에 **소환사 주문 탭을 신규 추가**하여 챔피언/아이템/소환사 주문을 한 화면에서 전환 조회 가능하도록 구성.
- **수집 범위 (모드 필터링)**: **소환사의 협곡 밸런스 패치 우선 수집** (아레나, 무작위 총력전, 클래식 등 번외 모드 섹션은 필터링하여 제외).
- **URL Fallback 전략**: 1차 생성 URL 요청 시 404 에러가 발생하면, 자동으로 대체 패턴(`league-of-legends-patch-` ↔ `patch-`)으로 재시도하는 자동 Fallback 메커니즘 적용.
- **외부 링크 버튼**: 별도의 외부 원문 보기 버튼은 추가하지 않음.

---

## 2. 현행 코드 분석 및 아키텍처 영향

### 2.1 관련 모듈 및 클래스
- **`:core:network`**:
  - `PathNoteUtil.kt`: URL 생성(Fallback 포함) 및 Next.js `__NEXT_DATA__` 기반 HTML 디코딩 파서
  - `NetworkPatchNoteType.kt`: `Spell("소환사 주문")` 추가 및 협곡 섹션 타겟팅 정교화
  - `SummonerSpellService.kt`: `getSpellPatchNoteList(version)` API 신설
- **`:core:data`**:
  - `SummonerSpellRepository.kt`: 주문 패치노트 캐시 및 네트워크 연동
- **`:core:domain`**:
  - `GetSpellPatchNoteList.kt`: UseCase 신규 추가
- **`:feature:champion`**:
  - `ChampionPatchNoteScreens.kt`: 상단 탭에 소환사 주문(`Spell`) 탭 추가 및 리스트 렌더링
  - `ChampionPatchNoteListViewModel.kt`: 소환사 주문 패치노트 상태(`SpellPatchNoteData`) Flow 연동

---

## 3. 상세 구현 계획

### 3.1 `:core:network` 파서 및 URL 고도화
1. **Next.js SSR 데이터 복원 파서 (`PathNoteUtil.kt`)**:
   - DOM에서 `#patch-notes-container` 탐색 실패 시, `script#__NEXT_DATA__` 태그의 JSON을 읽어 `richText.body`의 HTML을 `Jsoup.parse()`로 복원.
   - `isTargetSection` 판별 시 `소환사의 협곡` 외의 모드(`아레나`, `무작위 총력전`, `클래식`) 헤더는 스킵하여 **협곡 밸런스 패치만 엄격히 추출**.
2. **URL Fallback 메커니즘 (`PathNoteUtil.kt`)**:
   - `getPatchNoteUrlCandidates(version: String): List<String>` 함수를 통해 1순위(`league-of-legends-patch-X-Y-notes`), 2순위(`patch-X-Y-notes`), 3순위(과거 스플릿 패턴) 후보 URL 리스트 생성.
   - HTTP 요청 시 순차 시도하여 200 OK 페이지를 획득.
3. **타입 확장 (`NetworkPatchNoteType.kt`)**:
   - `enum class NetworkPatchNoteType(val patchName: String) { Champion("챔피언"), Item("아이템"), Rune("룬"), Spell("소환사 주문") }`

### 3.2 `:core:data` & `:core:domain` 레이어 확장
- `SummonerSpellRepository.getSpellPatchList(version: String): Flow<List<PatchNoteData>>`
- `GetSpellPatchNoteList(val spellRepository: SummonerSpellRepository)` UseCase 작성

### 3.3 `:feature:champion` UI 레이어 연동
- `ChampionPatchNoteScreens.kt`:
  - 탭 인덱스 0: 챔피언, 1: 아이템, 2: 소환사 주문
  - 소환사 주문 리스트 렌더링 아이템 컴포넌트 추가

---

## 4. 기능별 커밋 계획

1. `feat : 패치노트 Next.js __NEXT_DATA__ 파싱 및 URL Fallback 지원`
   - `PathNoteUtil.kt`, `NetworkPatchNoteType.kt`
2. `feat : 소환사 주문 패치노트 네트워크 서비스 및 UseCase 추가`
   - `SummonerSpellService.kt`, Repository, UseCase
3. `feat : 패치노트 화면에 소환사 주문 탭 추가`
   - `ChampionPatchNoteScreens.kt`, `ChampionPatchNoteListViewModel.kt`
4. `test : 최신 패치노트(16.x, 15.x) 협곡 챔피언/아이템/주문 크롤링 단위 테스트 보강`
   - `ConnectTest.kt`

---

## 5. 검증 계획

### 5.1 단위 테스트
- `ConnectTest.kt` 실행:
  - 16.17.1 (2026 최신 버전): 챔피언(브랜드, 초가스 등 협곡 챔피언만), 아이템(폭풍갈퀴, 갈라진 하늘) 정상 수집 여부 단언
  - 15.16.1 (2025 버전): 하위 호환성 확인

### 5.2 UI 검증
- 패치노트 화면 진입 시 상단 탭 3개(`챔피언`, `아이템`, `소환사 주문`) 정상 동작 및 스크롤 확인

---

## 6. LLM Wiki 갱신 계획

- `20_Wiki/Concepts/Riot_PatchNote_Crawler.md`: Next.js SSR 데이터 추출 구조 및 URL Fallback 정책 기술
- `20_Wiki/Entities/LOL_Champion_App.md` 및 `Indexes.md` 동기화
- `lint_wiki.ps1` 검증 (깨진 링크 0건 확인)
