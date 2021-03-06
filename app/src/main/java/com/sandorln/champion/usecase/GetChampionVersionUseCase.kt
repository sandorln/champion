package com.sandorln.champion.usecase

import com.sandorln.champion.repository.VersionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetChampionVersionUseCase(private val versionRepo: VersionRepository) {
    operator fun invoke(): Flow<String> = flow {
        emit(versionRepo.getLolChampionVersion())
    }.catch {
        emit("")
    }
}