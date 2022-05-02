package com.sandorln.champion.usecase

import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.model.type.AppSettingType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class HasNewLolVersionUseCase @Inject constructor(
    private val getVersionList: GetVersionListUseCase,
    private val getVersion: GetVersionUseCase,
    private val getAppSettingUseCase: GetAppSettingUseCase
) {
    operator fun invoke(): Flow<ResultData<Boolean>> = flow {
        emit(ResultData.Loading)
        val isOnQuestionNewestLolVersion = getAppSettingUseCase(AppSettingType.QUESTION_NEWEST_LOL_VERSION)
        if (isOnQuestionNewestLolVersion) {
            val version = getVersion().last()
            val versionList = getVersionList().last()

            val versionIndex = versionList.indexOfFirst { it == version }
            emit(ResultData.Success(versionIndex != 0))
        } else
            emit(ResultData.Success(false))
    }.catch {
        emit(ResultData.Failed(Exception(it), false))
    }.flowOn(Dispatchers.IO)
}