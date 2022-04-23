package com.sandorln.champion.usecase

import com.sandorln.champion.repository.VersionRepository

@Deprecated(
    "GetVersion 으로 변경," +
            "각 카테고리 별 Version 은 무의미 하다고 판단," +
            "통일된 버전으로 저장 및 불러오기"
)
class GetVersionCategory(
    private val versionRepository: VersionRepository
) {
    operator fun invoke() = versionRepository.getLolVersionCategory()
}