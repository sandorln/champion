---
name: llm-wiki
description: >-
  Andrej Karpathy의 LLM Wiki 디자인 패턴을 Antigravity 플랫폼의 파일 시스템 제어 및
  지식 그래프 거버넌스에 맞춤 변환하여 실행하는 스킬 세트입니다.
  /wiki-ingest, /wiki-query, /wiki-lint 명령어를 제공하며 사실 기반의 검증된
  마크다운 위키 지식 그래프를 영구 보존하고 유지 관리합니다.
---

# Antigravity LLM Wiki Management Platform

이 문서는 AI 에이전트(Antigravity)가 원본 소스(Layer 1)로부터 사실을 추출하여 구조화된 마크다운 위키 지식 그래프(Layer 2)를 빌드하고 영구 보존하기 위한 거버넌스 및 실행 규약(Contract)입니다.

---

## 1. 시스템 거버넌스 원칙 (Governance Rules)

1. **원본 데이터 불변성 (Layer 1 Immutability)**:
   - `10_Raw_Sources/` 내의 파일은 영구 불변(Read-Only)입니다.
   - 에이전트는 이 폴더 내의 파일을 절대 수정하거나 덮어쓰지 않습니다.
2. **상호 연결성 (Bidirectional Cross-Linking)**:
   - 모든 위키 문서(`20_Wiki/`)는 다른 개념/엔티티를 참조할 때 반드시 `[[문서명]]` (확장자 `.md` 제외) 형식을 준수합니다.
   - 링크 생성 시 실제 타겟 파일이 존재하는지 반드시 확인합니다.
3. **엄격한 팩트 신뢰도 태깅 (Fact Credibility Convention)**:
   - 모든 기술 내용은 다음 세 가지 신뢰도 태그 중 하나를 명시합니다:
     - `[EXTRACTED]`: 원본 소스에서 직접 발췌/인용한 사실
     - `[INFERRED]`: 논리적 흐름 및 코드 분석을 통해 에이전트가 도출한 지식
     - `[UNVERIFIED]`: 교차 검증이 필요하거나 출처가 불명확한 정보
4. **인덱스 동기화 (Index Synchronization)**:
   - 새 개념이나 엔티티가 추가/수정되면 반드시 `20_Wiki/Indexes.md`에 등록되어야 합니다.

---

## 2. 디렉터리 아키텍처 (Directory Architecture)

```text
.
├── .agents/skills/llm-wiki/
│   ├── SKILL.md                 # 본 스킬 정의서
│   └── scripts/
│       ├── lint_wiki.ps1        # 깨진 링크 및 고아 페이지 전수 감사 스크립트
│       └── hash_calc.ps1        # SHA256 중복 수집 방지 해시 계산 스크립트
├── 00_Inbox/                    # 신규 수집 대기 파일 (/wiki-ingest 대상)
├── 10_Raw_Sources/              # 불변 원본 보존 공간 (Immutable Raw Sources)
│   ├── Articles/                # 웹 기사, 블로그, 외부 기술 아티클
│   ├── Papers/                  # 연구 논문, 백서
│   └── Project_Docs/            # 프로젝트 코드베이스 추출 원본 명세서
├── 20_Wiki/                     # 지식 그래프 위키 공간 (Layer 2)
│   ├── Concepts/                # 핵심 이론, 원리, 아키텍처 개념 정의
│   ├── Entities/                # 프로젝트, 라이브러리, 기관, 인물
│   └── Indexes.md               # 지식 맵 및 전체 색인
└── 90_Logs/                     # 수집 로그 및 린트 결과 보관소
    ├── ingest_log.json          # 수집 완료된 소스의 SHA256 및 이력
    └── lint_report.md           # 린트 감사 결과 리포트
```

---

## 3. 커스텀 명령어 워크플로우 (Custom Tasks)

### Task 1: `/wiki-ingest`
- **트리거**: 사용자가 `/wiki-ingest`를 입력하거나 신규 문서 수집을 요청할 때.
- **실행 절차**:
  1. `00_Inbox/` 디렉터리의 수집 대상 파일 목록을 스캔합니다.
  2. 대상 파일의 SHA256 해시를 계산하여 `90_Logs/ingest_log.json`과 대조합니다 (`scripts/hash_calc.ps1` 활용 가능).
     - 이미 수집된 해시인 경우: 사용자에게 중복 파일임을 알리고 스킵합니다.
  3. 문서의 텍스트를 정밀 분석하여 핵심 **Concepts**(개념)와 **Entities**(개체)를 도출합니다.
  4. `20_Wiki/Concepts/` 또는 `20_Wiki/Entities/`에 기존 파일이 있다면 내용을 병합(Merge)하고, 없으면 신규 마크다운 문서를 작성합니다.
     - 각 정보는 `[EXTRACTED]`, `[INFERRED]`, `[UNVERIFIED]` 태그를 정확히 부착합니다.
     - 문서 하단에 출처(Source Reference: `10_Raw_Sources/...`)를 기록합니다.
  5. 상호 내부 링크(`[[개념명]]` 또는 `[[엔티티명]]`)를 양방향으로 연결합니다.
  6. `20_Wiki/Indexes.md`를 갱신하여 신규/수정 문서를 색인에 반영합니다.
  7. 처리가 완료된 원본 파일을 `10_Raw_Sources/` 내 적절한 서브디렉터리(`Articles/`, `Papers/`, `Project_Docs/`)로 이동(`Move`)시킵니다.
  8. `90_Logs/ingest_log.json`에 파일명, 해시, 처리 일시, 생성/수정된 위키 페이지 목록을 기록합니다.

### Task 2: `/wiki-query <질문내용>`
- **트리거**: 사용자가 `/wiki-query <질문내용>`을 입력하거나 위키 지식 탐색을 요청할 때.
- **실행 절차**:
  1. 질문의 핵심 키워드를 기반으로 `20_Wiki/` 내 관련 개념 및 엔티티 문서를 우선 검색합니다 (`grep_search` 또는 `view_file`).
  2. 일차 검색된 문서에 포함된 `[[링크]]` 네트워크를 추적하여 관련 인접 노드(Neighboring Nodes)들을 함께 수집합니다.
  3. 위키 문서에 인용된 `10_Raw_Sources/` 내의 원본 텍스트를 교차 확인하여 최신 사실 여부를 확인합니다.
  4. 답변 작성 규칙:
     - 허위 사실(Hallucination) 없이 검증된 사실만을 응답합니다.
     - 응답 본문에 참조한 `[[개념/엔티티]]` 링크 및 `10_Raw_Sources/` 출처 인용구를 포함합니다.
     - 사실의 확신도(`[EXTRACTED]`, `[INFERRED]`, `[UNVERIFIED]`)를 명시합니다.

### Task 3: `/wiki-lint`
- **트리거**: 사용자가 `/wiki-lint`를 입력하거나 위키 무결성 감사를 요청할 때.
- **실행 절차**:
  1. `.agents/skills/llm-wiki/scripts/lint_wiki.ps1`을 실행하거나 에이전트가 직접 `20_Wiki/` 내 모든 마크다운 파일을 순회합니다.
  2. **깨진 링크 검사**: `[[문서명]]`으로 작성되었으나 `20_Wiki/Concepts/` 또는 `20_Wiki/Entities/` 또는 `20_Wiki/`에 실제 해당 `.md` 파일이 없는 경우를 수집합니다.
  3. **고아 페이지 검사**: 다른 어떤 위키 문서나 `Indexes.md`에서도 참조되지 않는 페이지를 추출합니다.
  4. **신뢰도 태그 검사**: `[EXTRACTED]`, `[INFERRED]`, `[UNVERIFIED]` 규격을 누락한 문단이 있는지 점검합니다.
  5. 감사 결과를 `90_Logs/lint_report.md`에 상세히 기록합니다.
  6. 사용자에게 감사 결과 요약을 전달하고, 깨진 링크 연결이나 고아 페이지 색인화 등 자동 수정 진행 여부를 질문합니다.
