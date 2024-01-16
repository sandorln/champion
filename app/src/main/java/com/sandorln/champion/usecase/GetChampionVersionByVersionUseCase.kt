package com.sandorln.champion.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetChampionVersionByVersionUseCase @Inject constructor() {
    operator fun invoke(version: String): Flow<String> = flow {
        emit("")
    }.catch {
        emit("")
    }
}