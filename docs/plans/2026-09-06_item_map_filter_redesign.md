# [계획서] 아이템 맵 필터 세분화 (소환사의 협곡 / 칼바람 협곡 / 클래식 / 그 외)

- **작성 일자**: 2026-09-06
- **기준 브랜치**: feat/patch_note_crawling_fix (또는 develop 최신)
- **작업 브랜치**: feat/item_map_filter_redesign
- **문서 상태**: 검토 대기 (승인 전 구현 금지)

---

## 1. 개발 목표 및 배경
- **문제점**: 현재 아이템 목록에서 최신 소환사의 협곡 아이템, 과거 클래식 맵(시즌 1~4) 전용 아이템, 아레나(Map 30) 및 기타 모드 아이템이 '소환사의 협곡' 또는 '모두'에 함께 섞여서 표시되는 문제 발생.
- **원인**:
  1. `core/data/.../Map.kt`에서 `SUMMONER_RIFT_NAME`에 Map 1, 2(구 클래식 맵)와 Map 11(현대 협곡)을 하나로 묶어 판정.
  2. `maps`에 11, 12 키가 없거나 비어있는 특수 모드(아레나 등) 아이템의 경우 기본값이 `true`로 처리되어 `ALL`로 매핑됨.
- **목표**: 아이템 맵 필터를 사용자의 요구대로 다음 4가지로 명확히 분리:
  1. **소환사의 협곡 (Summoner's Rift)**: 현재 협곡(Map 11) 사용 가능 아이템
  2. **칼바람 협곡 (ARAM)**: 칼바람 나락(Map 12, 14) 사용 가능 아이템
  3. **소환사의 협곡(클래식)**: 과거 클래식 협곡(Map 1, 2) 전용 아이템
  4. **그 외 (Others)**: 아레나(Map 30), 돌격 넥서스(Map 21) 등 특수 모드 전용 아이템

---

## 2. 현행 코드 분석 및 아키텍처 영향

### 2.1 기존 맵 판정 로직 (`core/data/src/main/java/com/sandorln/data/util/Map.kt`)
```kotlin
private val SUMMONER_RIFT_NAME = listOf("1", "2", "11", "SummonersRift")
private val ARAM_NAME = listOf("12", "14")
```
- Riot Data Dragon의 맵 ID 체계:
  - `1`: Original Summoner's Rift (Summer) - 클래식
  - `2`: Original Summoner's Rift (Autumn) - 클래식
  - `11`: Summoner's Rift (현재 소환사의 협곡)
  - `12`: Howling Abyss (칼바람 나락)
  - `14`: Butcher's Bridge (칼바람 나락 이벤트)
  - `21`: Nexus Blitz (돌격 넥서스)
  - `30`: Arena (아레나)
- 문제: Map 1, 2를 현대 Map 11과 동일 취급하여 클래식 전용 아이템이 소환사의 협곡에 노출됨. 또한 아레나(Map 30) 등 키가 없을 때 기본값이 `true`로 빠져 `MapTypeEntity.ALL`로 오인식됨.

---

## 3. 상세 구현 계획

### 3.1 모델 레이어 (`core:model`)
- **[MODIFY] [`MapType.kt`](file:///C:/Users/SanDol/.gemini/antigravity/worktrees/champion/build_llm_wiki_system/core/model/src/main/java/com/sandorln/model/data/map/MapType.kt)**:
  ```kotlin
  enum class MapType {
      ALL,
      SUMMONER_RIFT,
      ARAM,
      CLASSIC_SUMMONER_RIFT,
      NONE
  }
  ```

### 3.2 데이터베이스 레이어 (`core:database`)
- **[MODIFY] [`ItemEntity.kt`](file:///C:/Users/SanDol/.gemini/antigravity/worktrees/champion/build_llm_wiki_system/core/database/src/main/java/com/sandorln/database/model/ItemEntity.kt)**:
  - 기존 Room DB ordinal 호환성을 유지하기 위해 뒤쪽에 추가:
  ```kotlin
  enum class MapTypeEntity {
      ALL,
      SUMMONER_RIFT,
      ARAM,
      NONE,
      CLASSIC_SUMMONER_RIFT
  }
  ```

### 3.3 데이터 매퍼 레이어 (`core:data`)
- **[MODIFY] [`Map.kt`](file:///C:/Users/SanDol/.gemini/antigravity/worktrees/champion/build_llm_wiki_system/core/data/src/main/java/com/sandorln/data/util/Map.kt)**:
  - 맵 ID 분리:
    - 현대 소환사의 협곡: `"11"`
    - 클래식 협곡: `"1"`, `"2"`, `"SummonersRift"`
    - 칼바람 나락: `"12"`, `"14"`
  - `asMapTypeEntity()` 로직:
    - `isModernSR`: Map 11이 true
    - `isAram`: Map 12 또는 14가 true
    - `isClassicSR`: Map 1 또는 2 또는 SummonersRift가 true
    - 판정 규칙:
      - `isModernSR && isAram` -> `ALL` (협곡 & 칼바람 공용)
      - `isModernSR && !isAram` -> `SUMMONER_RIFT` (협곡 전용)
      - `!isModernSR && isAram` -> `ARAM` (칼바람 전용)
      - `!isModernSR && !isAram && isClassicSR` -> `CLASSIC_SUMMONER_RIFT` (클래식 전용)
      - 그 외(아레나 등 Map 30만 있거나 전부 false) -> `NONE` (그 외)

### 3.4 아이템 기능 레이어 (`feature:item`)
- **[MODIFY] [`strings.xml`](file:///C:/Users/SanDol/.gemini/antigravity/worktrees/champion/build_llm_wiki_system/feature/item/src/main/res/values/strings.xml)**:
  ```xml
  <string name="map_type_classic_summoner_rift">소환사의 협곡(클래식)</string>
  ```
- **[MODIFY] [`TypeString.kt`](file:///C:/Users/SanDol/.gemini/antigravity/worktrees/champion/build_llm_wiki_system/feature/item/src/main/java/com/sandorln/item/util/TypeString.kt)**:
  - `MapType.CLASSIC_SUMMONER_RIFT -> R.string.map_type_classic_summoner_rift` 추가
- **[MODIFY] [`ItemHomeViewModel.kt`](file:///C:/Users/SanDol/.gemini/antigravity/worktrees/champion/build_llm_wiki_system/feature/item/src/main/java/com/sandorln/item/ui/home/ItemHomeViewModel.kt)**:
  - 필터링 로직:
    - `SUMMONER_RIFT`: `item.mapType == SUMMONER_RIFT || item.mapType == ALL`
    - `ARAM`: `item.mapType == ARAM || item.mapType == ALL`
    - `CLASSIC_SUMMONER_RIFT`: `item.mapType == CLASSIC_SUMMONER_RIFT`
    - `NONE`: `item.mapType == NONE`
    - `ALL`: 전체 아이템 표시
  - 과거 15.16.1 관련 임시 땜질 코드 제거 및 통합 정리

---

## 4. 기능별 커밋 계획
1. `feat : MapType에 소환사의 협곡(클래식) 추가 및 DB/Data 매핑 분리`
2. `feat : ItemHomeViewModel 맵별 아이템 필터링 로직 및 UI 태그 추가`
3. `test : 아이템 맵 분류(현대 협곡/칼바람/클래식/그 외) 단위 테스트 추가`
4. `docs : LLM Wiki 아이템 맵 필터 시스템 아키텍처 문서 동기화`

---

## 5. 검증 계획
- **단위 테스트**: 다양한 맵 ID 조합에 대해 올바른 `MapType`으로 매핑되는지 단위 테스트 검증
- **에뮬레이터 실화면 검증**:
  - 필터 다이얼로그에서 각 맵(소환사의 협곡, 칼바람 협곡, 클래식, 그 외) 선택 시 정확한 아이템 그룹만 필터링되는지 확인
