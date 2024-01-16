package com.sandorln.champion.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetVersionListUseCase @Inject constructor() {
    operator fun invoke(): Flow<List<String>> = flow {
        emit(emptyList())
    }
}