package com.sandorln.champion.usecase

import com.sandorln.champion.repository.VersionRepository

class ChangeVersion(private val versionRepository: VersionRepository) {
    suspend operator fun invoke(version : String) = versionRepository.changeLolVersion(version)
}