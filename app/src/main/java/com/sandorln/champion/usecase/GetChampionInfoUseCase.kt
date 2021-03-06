package com.sandorln.champion.usecase

import com.sandorln.champion.model.ChampionData
import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.repository.ChampionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetChampionInfoUseCase(
    private val championRepository: ChampionRepository,
    private val getLanguageCodeUseCase: GetLanguageCodeUseCase
) {
    operator fun invoke(championVersion: String, championId: String): Flow<ResultData<ChampionData>> =
        flow {
            emit(ResultData.Loading)
            val languageCode = getLanguageCodeUseCase()
            val championData = championRepository.getChampionInfo(championVersion, championId, languageCode)
            emit(ResultData.Success(championData))
        }.catch {
            emit(ResultData.Failed(Exception(it)))
        }
}