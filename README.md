# 롤 백과사전

라이엇에서 제공하는 API를 통해 모든 버전의 챔피언 & 아이템 & 소환사 주문 & 룬 정보를 확인할 수 있습니다.

[![Android App Badge](https://img.shields.io/badge/Platform-Android-green?style=for-the-badge&logo=android)](https://play.google.com/store/apps/details?id=com.sandorln.champion) [![Language Badge](https://img.shields.io/badge/Language-Kotlin-blue?style=for-the-badge&logo=kotlin)](https://kotlinlang.org/)
[![Tech Stack Badge](https://img.shields.io/badge/Tech-JetpackCompose-orange?style=for-the-badge&logo=android)](https://developer.android.com/jetpack/compose)

[![Site](https://img.shields.io/badge/스토어-이동하기-green?style=for-the-badge)](https://play.google.com/store/apps/details?id=com.sandorln.champion)
![Rating](https://img.shields.io/badge/평점-4.1%2F5.0-blue?style=for-the-badge)
![Downloads](https://img.shields.io/badge/다운로드-10K%2B-orange?style=for-the-badge)

<br>

## 💡 프로젝트 소개 (Introduction)
이 프로젝트는 리그 오브 레전드(League of Legends) 플레이어들이 게임 내 다양한 정보를 쉽고 편리하게 확인할 수 있도록 돕기 위해 개발된 안드로이드 앱입니다. 라이엇 게임즈(Riot Games)에서 제공하는 Data Dragon API를 활용하여, 게임의 최신 버전에 맞는 챔피언, 아이템, 소환사 주문, 룬 정보를 실시간으로 제공합니다.

기존에 산재되어 있던 정보를 한곳에 모아 사용자에게 직관적이고 효율적인 정보 탐색 경험을 제공하는 것을 목표로 합니다. 특히, 최신 Android 기술 스택인 Jetpack Compose를 적극적으로 활용하여 사용자 친화적인 UI와 뛰어난 성능을 구현하고자 했습니다.

### 주요 기능
* **모든 챔피언 정보 열람:** 챔피언별 스킬, 스토리, 스탯 등의 상세 정보 확인
* **아이템 정보 탐색:** 아이템 분류 및 상세 정보 확인
* **소환사 주문 및 룬 정보:** 각 주문과 룬의 효과 및 활용법 확인
* **아이템 초성 게임:** 아이템의 초성 및 설명을 통해 해당 아이템이 무엇인지 맞추는 게임
* **최신 버전 데이터:** 라이엇 API를 통해 항상 최신 게임 데이터 자동 업데이트

<br>

## 🛠️ 기술 스택 (Tech Stack)
이 프로젝트는 최신 Android 개발 트렌드를 반영하고 효율적인 아키텍처 패턴을 적용하여 개발되었습니다.

### 🚀 주요 기술 및 라이브러리 (Key Technologies & Libraries)

* **언어:** Kotlin
* **아키텍처:** MVI (Model-View-Intent), [Google Developer Architecture](https://developer.android.com/topic/architecture?hl=ko)
* **UI 프레임워크:** Jetpack Compose, Motion Layout
* **비동기 처리:** Kotlin Coroutines
* **의존성 주입:** Hilt
* **네트워크 통신:** Ktor Client
* **데이터베이스:** Room
* **이미지 및 미디어:** Glide, ExoPlayer
* **SDK 버전:** Min SDK 24 / Target SDK 35

<br>

## 🚀 주요 기능 및 스크린샷 (Features & Screenshots)
`롤 백과사전`은 리그 오브 레전드 플레이어가 필요로 하는 모든 게임 내 정보를 직관적이고 시각적으로 제공합니다.

### 🔍 챔피언 상세 정보
모든 챔피언의 스킬, 스토리, 기본 스탯은 물론, **패치 노트**까지 한눈에 확인할 수 있습니다. 강력한 검색 및 필터링 기능을 통해 원하는 챔피언을 빠르게 찾을 수 있습니다.

<img width="200" height="445" src="https://github.com/user-attachments/assets/aeee4e31-84e1-4167-8950-db3e2a970726" />
<img width="200" height="445" src="https://github.com/user-attachments/assets/3ea6d3a8-4d81-41d8-a94e-07ab7ca00a64" />

_챔피언 목록 및 상세 정보 화면_

### 📚 아이템 정보 탐색
다양한 카테고리별 아이템을 탐색하고, 각 아이템의 상세 스탯과 조합식, 그리고 **패치노트**를 쉽게 확인할 수 있습니다. 아이템 빌드 과정도 시각적으로 쉽게 파악할 수 있도록 구성했습니다.

<img width="200" height="445" src="https://github.com/user-attachments/assets/51be7dfe-cd3f-44a4-bcf7-f85805a4ef3b" />
<img width="200" height="445" src="https://github.com/user-attachments/assets/ad2b7565-0e3a-4a50-874c-9e9a6cd4cdd3" />
<img width="200" height="445" src="https://github.com/user-attachments/assets/00015f3c-ed38-4b35-a35c-68b18cc20b28" />

_아이템 목록과 조합식 그리고 필터 화면_

### ✨ 소환사 주문 & 룬 정보
각 소환사 주문과 룬의 효과와 추천 사용처를 상세하게 설명하여, 사용자에게 전략적인 게임 플레이에 필요한 정보와 **패치노트**를 제공합니다.

<img width="200" height="445" src="https://github.com/user-attachments/assets/62955d4e-6ae4-47ac-9172-7f189ef36277" />
<img width="200" height="445" src="https://github.com/user-attachments/assets/d438b638-b8f0-49ae-9680-10c97a84406c" />

_소환사 주문 및 룬 상세 화면_

### 🎮 아이템 초성 게임
리그 오브 레전드 아이템 이름을 맞히는 초성 퀴즈를 통해 게임 지식을 테스트하고, 숨겨진 아이템도 재미있게 학습할 수 있습니다. 짧은 시간 동안 즐길 수 있는 미니 게임으로 앱의 활용도를 높입니다.

<img width="200" height="445" src="https://github.com/user-attachments/assets/493b344d-6499-4cf7-aba2-837c77dfbf1a" />
<img width="200" height="445" src="https://github.com/user-attachments/assets/d90065c0-de0a-44dd-80a5-b36906d49fbe" />

_아이템 초성 게임 화면_

### 🔄 최신 게임 버전 데이터 자동 업데이트
라이엇 게임즈의 Data Dragon API를 활용하여, 새로운 패치나 업데이트가 있을 때마다 최신 게임 데이터를 자동으로 가져와 사용자에게 정확한 정보를 제공합니다.
