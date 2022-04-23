package com.sandorln.champion.usecase

import com.sandorln.champion.repository.VersionRepository
import kotlinx.coroutines.flow.Flow

class GetVersion(private val versionRepository: VersionRepository) {
    operator fun invoke() : Flow<String> = versionRepository.getLolVersion()
}