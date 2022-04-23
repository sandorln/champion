package com.sandorln.champion.usecase

import com.sandorln.champion.repository.VersionRepository
import kotlinx.coroutines.flow.Flow

class GetVersionList(private val versionRepository: VersionRepository) {
    operator fun invoke(): Flow<List<String>> = versionRepository.getLolVersionList()
}