# Sprite Sheet Optimization (스프라이트 시트 최적화)

- **유형**: 성능 최적화 기법 (Performance Optimization)
- **상위 카테고리**: 렌더링 및 네트워크 캐싱
- **관련 개념**: [[Riot_DataDragon_API]], [[MVI_Architecture]]
- **관련 엔티티**: [[LOL_Champion_App]], [[Jetpack_Compose]]

---

## 1. 개요 및 필요성
[EXTRACTED] 리그 오브 레전드에는 160개 이상의 챔피언, 수백 개의 아이템 및 룬 아이콘이 존재한다.
그리드 목록 화면에서 이 이미지들을 개별 HTTP 요청으로 로드하면:
1. 수백 번의 네트워크 왕복(RTT) 및 커넥션 스로틀링 발생
2. 이미지 캐시 관리 오버헤드로 인한 스크롤 버벅임(Jank)
3. 메모리 단편화 문제 발생

---

## 2. 해결 메커니즘
- [EXTRACTED] [[Riot_DataDragon_API]]에서 제공하는 스프라이트 시트(여러 아이콘이 격자형으로 병합된 단일 대형 이미지)를 백그라운드에서 1회 다운로드한다.
- [EXTRACTED] `core:data`의 `RefreshDownloadSpriteBitmap` 및 `GetSpriteBitmapByCurrentVersion` 유즈케이스가 로컬 디스크 및 메모리에 비트맵을 캐싱한다.
- [INFERRED] [[Jetpack_Compose]] 렌더링 시점에 전체 비트맵에서 해당 아이템/챔피언의 `(x, y, w, h)` 영역만을 추출(Sub-bitmap slicing)하여 Canvas에 고속 렌더링한다.

---

## 3. 최적화 효과
- [INFERRED] 네트워크 요청 횟수를 수백 회에서 단 몇 회(스프라이트 이미지 파일 수)로 대폭 축소 (90% 이상 절감).
- [INFERRED] Compose LazyVerticalGrid 스크롤 시 초당 60~120fps의 부드러운 프레임레이트 유지.

---

## 4. 출처 및 참고 문헌
- [EXTRACTED] `10_Raw_Sources/Project_Docs/LOL_Champion_Architecture_Spec.md`
- [EXTRACTED] `core/domain/src/main/java/com/sandorln/domain/usecase/sprite/`
