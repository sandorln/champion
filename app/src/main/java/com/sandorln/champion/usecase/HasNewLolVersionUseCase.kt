package com.sandorln.champion.usecase

import com.sandorln.model.result.ResultData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class HasNewLolVersionUseCase @Inject constructor(
    private val getVersionList: GetVersionListUseCase,
    private val getVersion: GetVersionUseCase,
    private val getAppSettingUseCase: GetAppSettingUseCase
) {
    operator fun invoke(): Flow<com.sandorln.model.result.ResultData<Boolean>> = flow {
        emit(com.sandorln.model.result.ResultData.Loading)
        val isOnQuestionNewestLolVersion = getAppSettingUseCase(com.sandorln.model.type.AppSettingType.QUESTION_NEWEST_LOL_VERSION)
        if (isOnQuestionNewestLolVersion) {
            val version = getVersion().last()
            val versionList = getVersionList().last()

            val versionIndex = versionList.indexOfFirst { it == version }
            emit(com.sandorln.model.result.ResultData.Success(versionIndex != 0))
        } else
            emit(com.sandorln.model.result.ResultData.Success(false))
    }.catch {
        emit(com.sandorln.model.result.ResultData.Failed(Exception(it), false))
    }.flowOn(Dispatchers.IO)
}