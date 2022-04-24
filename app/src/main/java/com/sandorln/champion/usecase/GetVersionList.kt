package com.sandorln.champion.usecase

import com.sandorln.champion.repository.VersionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetVersionList(private val versionRepository: VersionRepository) {
    operator fun invoke(): Flow<List<String>> =
        flow {
            emit(versionRepository.getLolVersionList())
        }.catch {
            emit(mutableListOf())
        }
}