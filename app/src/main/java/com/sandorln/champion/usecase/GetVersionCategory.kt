package com.sandorln.champion.usecase

import com.sandorln.champion.repository.VersionRepository

class GetVersionCategory(
    private val versionRepository: VersionRepository
) {
    operator fun invoke() = versionRepository.getLolVersionCategory()
}