package com.sandorln.champion.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetItemVersionUseCase @Inject constructor() {
    operator fun invoke(): Flow<String> = flow {
        emit("")
    }.catch {
        emit("")
    }
}