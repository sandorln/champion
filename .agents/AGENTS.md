# Antigravity Workspace Rules - LOL CHAMPION

이 프로젝트에서 작업할 때 다음 규칙과 아키텍처 가이드라인을 반드시 준수해야 합니다.

---

## 1. 프로젝트 아키텍처 및 폴더 계층 구조

본 프로젝트는 **Multi-Module** 구조를 채택하고 있으며, **Clean Architecture**와 **MVI(Model-View-Intent)** 패턴을 따릅니다.

### 모듈 구조 (Module Architecture) 및 의존성 흐름
의존성은 단방향으로만 흘러야 하며, 상위 레이어(Presentation/UI)가 하위 레이어(Data/Network/Database)의 구체적인 구현체에 직접 의존해서는 안 됩니다.

```mermaid
graph TD
    subgraph Feature Modules [Feature Modules]
        feature_home[":feature:home"]
        feature_champ[":feature:champion"]
        feature_item[":feature:item"]
        feature_etc["..."]
    end

    subgraph Core Modules [Core Modules]
        core_domain[":core:domain (UseCase)"]
        core_model[":core:model (Domain Entity)"]
        core_data[":core:data (Repository Impl)"]
        core_network[":core:network (Ktor DTO)"]
        core_database[":core:database (Room Entity)"]
        core_datastore[":core:datastore"]
        core_design[":core:design (Design System)"]
    end

    app[":app (Application Entry / DI Bindings)"]

    %% App dependencies
    app --> Feature Modules
    app --> Core Modules

    %% Feature dependencies
    Feature Modules --> core_domain
    Feature Modules --> core_model
    Feature Modules --> core_design

    %% Domain dependencies
    core_domain --> core_model

    %% Data dependencies
    core_data --> core_domain
    core_data --> core_model
    core_data --> core_network
    core_data --> core_database
    core_data --> core_datastore
```

### 각 모듈별 상세 역할 및 데이터 흐름 규칙

1. **`core:model` (Domain/Shared Models)**
   * 프로젝트 전체에서 공유하는 순수 데이터 모델(Domain Entity)을 정의합니다. (예: `SummaryChampion`, `PatchNoteData`)
   * Android SDK, Ktor, Room 등 **특정 프레임워크나 라이브러리에 종속적인 어노테이션이나 클래스를 포함해서는 안 됩니다.**

2. **`core:network` (Remote Data Source)**
   * 원격 API 통신(Ktor 등)을 담당하며, API 스펙에 맞는 Network DTO를 정의합니다.
   * 이 모듈의 모델(DTO)은 외부 통신용이므로 외부 라이브러리 어노테이션(예: `@Serializable`)을 가질 수 있으나, 이 모듈 외부로 노출되지 않도록 설계합니다.

3. **`core:database` (Local Data Source)**
   * 로컬 DB(Room) 및 DAO를 관리하며, 데이터베이스 스키마를 표현하는 DB Entity를 정의합니다.
   * 이 모듈의 모델은 로컬 DB용이므로 Room 어노테이션(예: `@Entity`, `@PrimaryKey`)을 가집니다.

4. **`core:data` (Data Repository Layer)**
   * `core:domain`에 정의된 Repository 인터페이스의 실제 구현체가 위치합니다.
   * **데이터 맵핑(Mapping):** `core:network`에서 받아온 DTO나 `core:database`에서 쿼리한 DB Entity를 **확장 함수(Mapper)**를 통해 `core:model`에 정의된 Domain Model로 변환합니다.
   * **주의:** `core:data` 레이어는 데이터 변환 및 중개 역할만 수행하며, 상위 UI 레이어는 이 모듈에 직접 의존하지 않습니다.

5. **`core:domain` (Business Logic Layer)**
   * 순수 비즈니스 로직(UseCase)과 Repository 인터페이스를 정의합니다.
   * 특정 프레임워크나 구현 디테일(Retrofit, Room 등)에 종속되지 않는 순수 Kotlin 모듈입니다.

6. **`feature:*` (Presentation Layer)**
   * Jetpack Compose UI 및 MVI 패턴 기반의 ViewModel이 존재합니다.
   * **데이터 수신:** ViewModel은 `core:domain`의 UseCase를 주입받아 비즈니스 로직을 호출하고, 결과값으로 `core:model`에 정의된 Domain Model을 수집(Collect)합니다.

---

## 2. 디자인 패턴 가이드라인

### MVI (Model-View-Intent) 패턴 규칙
새로운 기능을 추가하거나 기존 화면을 수정할 때 반드시 MVI 패턴의 구조를 지켜야 합니다.

1. **State (UI State)**
   * 화면의 모든 상태를 하나의 불변 `data class`로 관리합니다. (예: `ChampionHomeUiState`)
   * ViewModel 내에서 `MutableStateFlow`를 통해 상태를 변경(`update`)하고, 외부에는 `StateFlow`로 노출합니다.
2. **Action (Intent)**
   * 사용자의 입력이나 이벤트를 표현하는 `sealed interface`를 정의합니다. (예: `ChampionHomeAction`)
   * UI에서는 ViewModel의 `sendAction(action)` 메서드만을 통해 이벤트를 전달합니다.
3. **SideEffect**
   * 화면 전환, 에러 메시지 토스트 표시 등 단발성 이벤트를 표현하는 `sealed interface`를 정의합니다. (예: `ChampionHomeSideEffect`)
   * `MutableSharedFlow`를 통해 방출하고, Compose UI에서는 `LaunchedEffect` 등을 사용하여 수집(`collect`)합니다.

### 의존성 주입 (Dependency Injection)
* **Hilt**를 사용하여 의존성을 주입합니다.
* ViewModel 클래스에는 `@HiltViewModel` 및 `@Inject constructor`를 적용하고, 생성자 파라미터로 필요한 **UseCase**들만을 주입받아 사용합니다. (Repository를 직접 주입받지 않고 Domain 영역의 UseCase를 거치도록 함)

### 비동기 처리
* **Kotlin Coroutines**(`viewModelScope`, `launch`)를 사용하여 비동기 작업을 처리하며, 데이터 스트림은 **Flow**(`StateFlow`, `SharedFlow`, `combine` 등)를 활용하여 선언형으로 처리합니다.

---

## 3. 절대로 하지 말아야 할 것들 (Avoid/Anti-Patterns)

1. **레이어 의존성 규칙 위반 (Bypassing UseCases)**
   * ❌ **금지:** `feature` 모듈의 ViewModel에서 `Repository`를 직접 주입받아 사용하는 행위.
   * 🟢 **권장:** 항상 `core:domain`의 `UseCase`를 거쳐서 데이터에 접근해야 합니다.

2. **도메인 모델과 데이터 모델의 혼용 (Model Leakage)**
   * ❌ **금지:** `@Entity` 등의 Room 어노테이션이 붙은 클래스를 `core:model`에 위치시키거나, UI 레이어까지 직접 반환하는 행위.
   * ❌ **금지:** Network DTO(예: `@Serializable` 어노테이션이 지정된 클래스)를 UI 레이어까지 끌고 올라와 화면에 보여주는 행위.
   * 🟢 **권장:** Network DTO와 Database Entity는 각 모듈 내부(`core:network`, `core:database`)에서만 존재하고, `core:data`에서 Mapper를 거쳐 무조건 순수한 `core:model`로 변환된 후 외부 레이어로 넘어가야 합니다.

3. **피처 간 직접 의존성 추가 (Feature-to-Feature Dependency)**
   * ❌ **금지:** 하나의 `:feature:*` 모듈이 다른 `:feature:*` 모듈에 직접 의존성을 맺어 호출하는 행위.
   * 🟢 **권장:** 피처 간 화면 전환이나 네비게이션이 필요한 경우, 공통 모듈에 선언된 인터페이스를 주입받아 구현하거나 `:app` 모듈 단에서 조정하도록 처리합니다.

4. **비즈니스 로직의 UI 레이어 유출 (Business Logic in UI)**
   * ❌ **금지:** `Screen.kt`나 `ViewModel` 내에서 원시 데이터 필터링이나 데이터 가공 로직을 비대하게 작성하는 행위.
   * 🟢 **권장:** 데이터 가공 및 필터링 비즈니스 로직은 `UseCase` 내에서 처리한 후 결과만을 UI State로 발행해야 합니다.

---

## 4. 계획 및 구현 절차 규칙

사용자가 **"계획을 세우자"** 또는 이와 유사한 기획/설계 관련 요청을 하는 경우, 계획서의 생명주기(Lifecycle)에 따라 다음과 같이 폴더를 분류하여 처리하고 개발을 통제해야 합니다.

1. **1단계: 작성 및 리뷰 대기 (Draft)**
   * 에이전트가 계획서를 초안 작성하거나 사용자의 검토를 기다릴 때 생성하는 경로입니다.
   * 경로: `docs/plans/draft/{yyyy-MM-dd}-{기능내용}.md`
     * *예시: `docs/plans/draft/2026-07-19-add-champion-filter.md`*
   * **⚠️ 중요:** 사용자의 명시적인 승인/리뷰 완료 처리가 있을 때까지 **절대로 실제 기능 구현이나 코드 수정을 시작해서는 안 됩니다.**

2. **2단계: 리뷰 완료 및 대기 (Ready)**
   * 사용자의 승인(리뷰 완료)을 받은 후, 실제 구현 시작 직전의 준비 상태일 때 파일을 이 폴더로 이동합니다.
   * 경로: `docs/plans/ready/{yyyy-MM-dd}-{기능내용}.md`

3. **3단계: 개발 진행 중 (In Progress)**
   * 승인된 계획을 바탕으로 실제 기능 개발 및 코드 수정을 시작할 때 파일을 이 폴더로 이동합니다.
   * 경로: `docs/plans/in-progress/{yyyy-MM-dd}-{기능내용}.md`

4. **4단계: 완료 또는 드랍 (Completed / Dropped)**
   * 개발 및 검증이 최종 완료되었거나 중간에 드랍(Drop)된 경우 파일을 이 폴더로 이동하여 기록으로 남깁니다.
   * 경로: `docs/plans/completed/{complete 또는 drop}-{yyyy-MM-dd}-{기능내용}.md`
     * *예시: `docs/plans/completed/complete-2026-07-19-add-champion-filter.md`*
     * *예시: `docs/plans/completed/drop-2026-07-19-add-champion-filter.md`*
