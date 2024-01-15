package com.sandorln.champion.usecase

import com.sandorln.model.ChampionData
import com.sandorln.model.result.ResultData
import com.sandorln.champion.repository.ChampionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetChampionInfoUseCase(
    private val championRepository: ChampionRepository,
    private val getLanguageCodeUseCase: GetLanguageCodeUseCase
) {
    operator fun invoke(championVersion: String, championId: String): Flow<com.sandorln.model.result.ResultData<com.sandorln.model.ChampionData>> =
        flow {
            emit(com.sandorln.model.result.ResultData.Loading)
            val languageCode = getLanguageCodeUseCase()
            val championData = championRepository.getChampionInfo(championVersion, championId, languageCode)
            emit(com.sandorln.model.result.ResultData.Success(championData))
        }.catch {
            emit(com.sandorln.model.result.ResultData.Failed(Exception(it)))
        }
}