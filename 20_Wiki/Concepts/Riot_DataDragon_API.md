# Riot Data Dragon API

- **유형**: 웹 서비스 및 CDN API (Web Service & CDN)
- **제공자**: [[Riot_Games]]
- **관련 프로젝트**: [[LOL_Champion_App]]
- **관련 개념**: [[Sprite_Sheet_Optimization]]
- **사용 라이브러리**: [[Ktor]], [[Room]]

---

## 1. 개요 및 구조
[EXTRACTED] Data Dragon(DDragon)은 라이엇 게임즈가 개발자 및 서드파티 앱을 위해 제공하는 공식 리그 오브 레전드 정적 게임 데이터 및 이미지 에셋 CDN 서비스이다.

- 엔드포인트 기본 구조: `https://ddragon.leagueoflegends.com/cdn/`
- 주요 리소스:
  - 버전 목록: `api/versions.json`
  - 챔피언 요약/상세 정보: `cdn/{version}/data/{locale}/champion.json` 및 `champion/{championId}.json`
  - 아이템 정보: `cdn/{version}/data/{locale}/item.json`
  - 룬 정보: `cdn/{version}/data/{locale}/runesReforged.json`
  - 스프라이트 시트 이미지: `cdn/{version}/img/sprite/`

---

## 2. 버전 관리 메커니즘
- [EXTRACTED] 리그 오브 레전드는 약 2주 간격으로 신규 패치를 릴리스하며, 각 패치마다 고유한 버전 문자열(예: `14.5.1`)을 부여한다.
- [INFERRED] [[LOL_Champion_App]]은 구동 시 최신 버전 목록을 확인하고, 로컬 캐시(Room/DataStore)의 버전과 비교하여 증분(Delta) 데이터만 갱신하는 방식을 채택하고 있다.

---

## 3. 에셋 서빙 전략
- [EXTRACTED] 개별 챔피언/아이템/스킬 이미지를 1개씩 요청하면 HTTP 연결 오버헤드가 급증하므로, 라이엇은 묶음 이미지인 [[Sprite_Sheet_Optimization|스프라이트 시트]]를 제공한다.
- [EXTRACTED] JSON 메타데이터에 각 이미지의 스프라이트 파일명(`sprite`), 좌표(`x`, `y`), 크기(`w`, `h`) 정보가 포함된다.

---

## 4. 공식 웹사이트 패치 노트 URL 규격
라이엇 공식 홈페이지의 패치 노트 URL은 시즌별 / 마이너 버전별로 규칙이 상이하므로 클라이언트에서 웹 스크래핑 및 링크 연결 시 다음 규칙을 적용한다:
- **Major >= 16 (시즌 26 이후)**:
  - Minor >= 4: `https://www.leagueoflegends.com/ko-kr/news/game-updates/league-of-legends-patch-{major + 10}-{minor}-notes/`
  - Minor 1..3: `https://www.leagueoflegends.com/ko-kr/news/game-updates/patch-{major + 10}-{minor}-notes/`
- **Major 15 (시즌 25)**:
  - Minor 1..2: `patch-25-s1-{minor}-notes/`
  - Minor 3: `patch-2025-s1-3-notes/`
  - Minor >= 4: `patch-25-{minor:02d}-notes/`
- **Major <= 14**:
  - `patch-{major}-{minor}-notes/`

---

## 5. 챔피언 스킨 및 스플래시 이미지 규격
라이엇 Data Dragon의 챔피언 상세 API(`champion/{championId}.json`) 및 스플래시 이미지 CDN 연동 규칙:
- **스플래시 이미지 엔드포인트**: `https://ddragon.leagueoflegends.com/cdn/img/champion/splash/{championId}_{num}.jpg`
- **스킨 데이터 모델 규격**:
  - `num`: 고유 스킨 식별 번호 (JSON 상에서 **정수형(`Int`)**으로 제공됨, 예: `0`, `1`, `2`).
  - `name`: 스킨 명칭 (스킨 번호 `0`의 명칭은 `"default"`로 제공되므로 클라이언트에서 "기본 스킨"으로 로컬라이징 처리).
- **최신 버전 크로마(Chroma) 포함 및 필터링 정책**:
  - 최신 버전(16.x 이상)부터는 `skins` 배열 내에 색상 변형인 크로마 항목들이 함께 포함되어 제공된다.
  - 크로마 항목은 고유 속성으로 `"parentSkin": {부모스킨번호}`를 포함한다.
  - **주의**: 라이엇 CDN은 개별 크로마에 대한 스플래시 이미지 에셋을 제공하지 않으며, 요청 시 **403 Forbidden**을 반환한다.
  - 따라서 챔피언 상세 화면의 스킨 갤러리 구성 시에는 `parentSkin == null`인 고유 스킨만 필터링하여 노출해야 정상적인 이미지를 렌더링할 수 있다.

---

## 6. 출처 및 참고 문헌
- [EXTRACTED] `10_Raw_Sources/Project_Docs/LOL_Champion_Architecture_Spec.md`
- [EXTRACTED] Riot Games Developer Portal Data Dragon Documentation
- [EXTRACTED] 라이엇 게임즈 공식 웹사이트 패치 뉴스 URL 구조 실측 분석 (2026-09)
- [EXTRACTED] Data Dragon 16.17.1 챔피언 상세 스킨 스키마 및 CDN 스플래시 응답 실측 검증 (2026-09)
