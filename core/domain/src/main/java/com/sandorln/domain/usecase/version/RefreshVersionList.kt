package com.sandorln.domain.usecase.version

import com.sandorln.data.repository.version.VersionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefreshVersionList @Inject constructor(
    private val versionRepository: VersionRepository
) {
    suspend operator fun invoke() = versionRepository.refreshVersionList()
}