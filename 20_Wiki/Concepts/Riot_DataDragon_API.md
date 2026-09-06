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

## 4. 출처 및 참고 문헌
- [EXTRACTED] `10_Raw_Sources/Project_Docs/LOL_Champion_Architecture_Spec.md`
- [EXTRACTED] Riot Games Developer Portal Data Dragon Documentation
