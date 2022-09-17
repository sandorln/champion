package com.sandorln.champion.usecase

import com.sandorln.champion.repository.VersionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetChampionVersionByVersionUseCase @Inject constructor(
    private val versionRepository: VersionRepository
) {
    operator fun invoke(version: String): Flow<String> = flow {
        emit(versionRepository.getLolChampionVersionByVersionName(version))
    }.catch {
        emit("")
    }
}