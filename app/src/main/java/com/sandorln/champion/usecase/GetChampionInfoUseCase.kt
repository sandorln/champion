package com.sandorln.champion.usecase

import com.sandorln.model.ChampionData
import com.sandorln.model.result.ResultData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetChampionInfoUseCase @Inject constructor() {
    operator fun invoke(championVersion: String, championId: String): Flow<ResultData<ChampionData>> =
        flow {
            emit(ResultData.Loading)
            emit(ResultData.Success(ChampionData()))
        }.catch {
            emit(ResultData.Failed(Exception(it)))
        }
}