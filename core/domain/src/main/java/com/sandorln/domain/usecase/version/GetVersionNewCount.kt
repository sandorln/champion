package com.sandorln.domain.usecase.version

import com.sandorln.data.repository.version.VersionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetVersionNewCount @Inject constructor(
    private val versionRepository: VersionRepository
) {
    suspend operator fun invoke(versionName: String, preVersionName: String) =
        versionRepository.getVersionNewCount(versionName, preVersionName)
}