package com.sandorln.champion.usecase

import com.sandorln.champion.model.result.ResultData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class HasNewLolVersionUseCase @Inject constructor(
    private val getVersionList: GetVersionListUseCase,
    private val getVersion: GetVersionUseCase
) {
    operator fun invoke(): Flow<ResultData<Boolean>> = flow {
        emit(ResultData.Loading)
        val version = getVersion().last()
        val versionList = getVersionList().last()
        val versionIndex = versionList.indexOfFirst { it == version }
        emit(ResultData.Success(versionIndex != 0))
    }.catch {
        emit(ResultData.Failed(Exception(it), false))
    }.flowOn(Dispatchers.IO)
}