package com.sandorln.domain.usecase.version

import com.sandorln.data.repository.version.VersionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAllVersionList @Inject constructor(
    private val versionRepository: VersionRepository
) {
    operator fun invoke() = versionRepository.allVersionList
}