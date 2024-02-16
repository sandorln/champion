package com.sandorln.domain.usecase.version

import com.sandorln.data.repository.version.VersionRepository
import com.sandorln.model.data.version.Version
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetPreviousVersion @Inject constructor(
    private val versionRepository: VersionRepository
) {
    suspend operator fun invoke(version: String): Version? {
        val allVersionList = versionRepository.allVersionList.firstOrNull() ?: return null
        val previousVersionIndex = allVersionList.indexOfFirst { it.name == version } + 1
        return allVersionList.getOrNull(previousVersionIndex)
    }
}