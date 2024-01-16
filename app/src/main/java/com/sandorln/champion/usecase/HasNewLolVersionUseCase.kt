package com.sandorln.champion.usecase

import com.sandorln.model.result.ResultData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HasNewLolVersionUseCase @Inject constructor() {
    operator fun invoke(): Flow<ResultData<Boolean>> = flow {
        emit(ResultData.Loading)
        emit(ResultData.Success(false))
    }.catch {
        emit(ResultData.Failed(Exception(it), false))
    }.flowOn(Dispatchers.IO)
}