# [계획서] 신규 게임 모드 추가 및 DB 데이터 활용 확장 계획

- **작성 일자**: 2026-09-06
- **기준 브랜치**: develop (최신)
- **작업 브랜치**: feat/game_feature_expansion
- **문서 상태**: 검토 대기 (승인 전 구현 금지 - `[구현]` 승인 대기)

---

## 1. 개발 목표 및 배경

### 1.1 배경 및 목적
- 현재 `feature/game` 모듈에는 **"아이템 초성 퀴즈(Initial Quiz)"** 1종만 단독으로 제공되고 있습니다.
- 로컬 SQLite(Room) 데이터베이스에는 챔피언 상세 스탯(사거리, 이동속도, 체력 등), 아이템 조합 관계(`from`/`into`), 가격, 소환사 주문, 룬 등 방대한 리그 오브 레전드 데이터와 오프라인 스프라이트 이미지 캐시가 이미 구축되어 있습니다.
- 사용자 요청에 따라, 현재 DB의 풍부한 데이터를 활용하여 추가할 수 있는 신규 게임 후보를 분석하고, 사용자의 선택에 맞춰 확장 가능한 게임 허브 구조 및 신규 게임 구현 계획을 수립합니다.

---

## 2. 현행 데이터베이스(Room) 보유 데이터 분석

| 엔티티 | 주요 속성 | 보유 정보 및 게임 기획 활용 포인트 |
| :--- | :--- | :--- |
| **`ChampionEntity`** | `name`, `title`, `tags`, `partype`, `stats`, `info`, `image` | - **칭호(`title`)**: "데마시아의 힘", "구미호" 등 칭호 매칭<br>- **스탯(`stats`)**: 기본 사거리(`attackrange`), 이동속도, 체력, 공격력 등 수치 비교<br>- **자원(`partype`)**: 마나, 기력, 분노, 열기, 피의 샘, 마나 없음<br>- **역할군(`tags`)**: Fighter, Tank, Mage, Assassin, Marksman, Support<br>- **난이도(`info.difficulty`)**: 1~10 난이도 지표 |
| **`ItemEntity`** | `name`, `from`, `into`, `depth`, `gold`, `tags`, `description`, `image` | - **조합식(`from`/`into`)**: 하위 재료 목록 및 상위 완성품 계층 구조 (`depth`)<br>- **가격(`gold.total`/`gold.base`)**: 총 구매가 및 조합 비용<br>- **효과(`description`)**: 고유 지속 효과 및 사용 효과 텍스트 |
| **`RuneStyleEntity` / `RuneDataEntity`** | `name`, `slots`, `shortDesc`, `longDesc`, `icon` | - **룬 이름/효과**: 정복자, 감전, 난입 등 핵심 룬과 일반 룬<br>- **계열**: 정밀, 지배, 마법, 결의, 영감 |
| **`SummonerSpellEntity`** | `name`, `cooldownBurn`, `description`, `image` | - **쿨다운(`cooldownBurn`)**: 점멸(300초), 순간이동 등 쿨다운 비교 |

---

## 3. 신규 게임 후보 제안 (5가지 옵션)

### 🥇 옵션 1. 아이템 조합식 완성 퀴즈 (Item Recipe Quiz) — [강력 추천]
- **게임 방식**:
  - **모드 A (조합 재료 맞추기)**: 완성 아이템(예: "삼위일체", "무한의 대검")의 아이콘과 이름을 보고, 하단에 나열된 재료 아이템 후보 중 올바른 하위 조합 재료(예: 광휘의 검 + 점화석 + 곡괭이)를 모두 골라 슬롯에 넣어 완성.
  - **모드 B (완성템 추리)**: 재료 아이템 2~3개(예: B.F. 대검 + 곡괭이 + 민첩성의 망토)를 제시하고, 조합 가능한 상위 아이템을 4지선다에서 선택.
- **데이터 활용**: `ItemEntity.from`, `into`, `depth`, `image` (스프라이트 캐시 100% 활용)
- **특징**: 롤 유저들이 가장 지식을 뽐내기 좋고 성취감이 높은 대표적인 인기 퀴즈.

### 🥈 옵션 2. 사거리 / 스탯 / 가격 "더 높게? 더 낮게?" (Higher or Lower) — [추천]
- **게임 방식**:
  - 두 대상이 양옆에 등장하여, 기준 대상 대비 우측 대상의 수치가 더 높은지(Higher) 낮은지(Lower)를 맞추며 연승(Streak)을 쌓는 스피드 퀴즈.
  - **챔피언 사거리 대결**: 케이틀린(650) vs 애쉬(600)?
  - **아이템 가격 대결**: 라바돈의 죽음모자(3600G) vs 삼위일체(3333G)?
  - **기본 이동속도 대결**: 판테온(345) vs 마스터 이(355)?
- **데이터 활용**: `ChampionEntity.stats.attackrange`, `stats.movespeed`, `ItemEntity.gold.total`
- **특징**: 모바일 터치 환경에서 1초 만에 판단하는 직관적이고 중독성 높은 하이퍼 캐주얼 게임.

### 🥉 옵션 3. 롤들(LoLdle) 스타일 챔피언 속성 추리 게임
- **게임 방식**:
  - 무작위로 선정된 비밀 챔피언 1명을 5~6번의 기회 안에 맞추는 단서 추리 게임.
  - 플레이어가 챔피언을 추측해 입력할 때마다 속성(역할군, 주 자원 종류, 사거리 분류, 기본 체력)에 대해 일치(초록), 부분 일치(노랑), 불일치(빨강), 상/하(화살표) 피드백 제공.
- **데이터 활용**: `ChampionEntity`의 `tags`, `partype`, `stats`, `image`
- **특징**: 퍼즐형 두뇌 플레이를 선호하는 사용자층에게 큰 인기.

### 🏅 옵션 4. 챔피언 초성 퀴즈 (Champion Initial Quiz)
- **게임 방식**:
  - 기존의 "아이템 초성 퀴즈" 엔진과 UI(`HangulUtil`, 타이머, 콤보, 채점 로직)를 그대로 활용.
  - 챔피언의 칭호(`title`)를 힌트로 제공하고 초성을 맞춤 (예: "데마시아의 힘 : ㄱㄹ" ➡️ "가렌").
- **데이터 활용**: `ChampionEntity.name`, `title`, `image`
- **특징**: 개발 공수가 가장 적고, 기존 게임 시스템과 완벽하게 융합 가능.

### 🏅 옵션 5. 챔피언 칭호 매칭 퀴즈 (Title Match Quiz)
- **게임 방식**:
  - 이색적이고 멋진 챔피언 칭호(예: "어둠 속의 방랑자", "죽음을 노래하는 자", "황혼의 눈")를 보고 4명의 챔피언 중 올바른 챔피언을 고르는 4지선다 타임어택.
- **데이터 활용**: `ChampionEntity.title`, `name`, `image`

---

## 4. UI 및 아키텍처 확장 계획 (Game Hub 구조)

현재 `GameHomeScreen.kt`는 초성 게임 단일 화면으로 고정되어 있으므로, 여러 게임을 선택할 수 있는 **게임 허브(Game Hub) 런처 형태**로 확장합니다.

```mermaid
flowchart TD
    Home["GameHomeScreen (게임 허브)"] --> G1["초성 퀴즈 (아이템 / 챔피언)"]
    Home --> G2["신규: 아이템 조합식 퀴즈 (Item Recipe)"]
    Home --> G3["신규: Higher or Lower (스탯/가격 비교)"]
    
    subgraph Data Layer
        DB[(Room AppDatabase)]
        DB --> ChampionEntity
        DB --> ItemEntity
        DB --> SummonerSpellEntity
    end
    
    Data Layer --> G1
    Data Layer --> G2
    Data Layer --> G3
```

### 4.1 컴포넌트별 상세 구현 계획 (아이템 조합식 퀴즈 1순위 기준)
1. **Core Domain (`core/domain`)**:
   - `GetItemRecipeQuizUseCase.kt`: `from` 속성을 가진 완성 아이템과 해당 하위 조합 재료 목록, 그리고 보기용 오답 아이템 풀을 무작위 추출하여 라운드별 퀴즈 데이터를 생성하는 유즈케이스.
2. **Feature Game (`feature/game`)**:
   - `GameHomeScreen.kt`: 단일 초성 게임 화면 ➡️ 게임 모드 카드 그리드/리스트 허브 UI로 리팩터링.
   - `navigation/GameNavigation.kt`: 신규 게임 네비게이션 라우트 추가.
   - `ui/recipequiz/ItemRecipeQuizScreen.kt`: 문제 표시 영역(완성 아이템), 유저 선택 슬롯, 후보 아이템 선택 그리드 UI 구현.
   - `ui/recipequiz/ItemRecipeQuizViewModel.kt`: 라운드 진행, 조합 완성 체크, 점수 계산, 결과 상태 관리 (MVI 패턴).

---

## 5. 기능별 커밋 계획

1. `feat : 게임 허브 대시보드 UI 및 네비게이션 확장 구조 구성`
2. `feat : 신규 게임 도메인 모델 및 퀴즈 생성 UseCase 구현`
3. `feat : 신규 게임 ViewModel 및 MVI 상태 머신 구현`
4. `feat : 신규 게임 Screen UI 컴포저블 및 애니메이션 구현`
5. `feat : 게임 종료 결과 다이얼로그 및 점수 연동`

---

## 6. 검증 계획

- **빌드 검증**: `./gradlew assembleDebug`
- **단위 테스트**: 신규 UseCase 및 ViewModel 비즈니스 로직 테스트 (`./gradlew testDebugUnitTest`)
- **동작 검증**: 게임 시작 -> 문제 출제 -> 선택 및 제출 -> 점수/콤보 획득 -> 게임 종료 팝업 정상 플로우 확인

---

## 7. LLM Wiki 갱신 계획

- `20_Wiki/Concepts/Game_Mode_Architecture.md` 신규 작성
- `20_Wiki/Entities/LOL_Champion_App.md` 신규 게임 기능 명세 반영
- `20_Wiki/Indexes.md` 인덱스 갱신 및 무결성 검증
