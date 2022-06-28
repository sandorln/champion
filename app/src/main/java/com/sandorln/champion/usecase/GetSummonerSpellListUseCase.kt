package com.sandorln.champion.usecase

import com.sandorln.champion.model.SummonerSpell
import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.repository.SummonerSpellRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

class GetSummonerSpellListUseCase(
    private val getVersionUseCase: GetVersionUseCase,
    private val getLanguageCodeUseCase: GetLanguageCodeUseCase,
    private val summonerSpellRepository: SummonerSpellRepository
) {
    operator fun invoke(): Flow<ResultData<List<SummonerSpell>>> =
        getVersionUseCase()
            .flatMapLatest { totalVersion ->
                flow {
                    emit(ResultData.Loading)
                    /* 너무 빠르게 진행 시 해당 값이 무시됨 */
                    delay(250)
                    val languageCode = getLanguageCodeUseCase()
                    val summonerSpellList = summonerSpellRepository.getSummonerSpellList(
                        summonerSpellVersion = totalVersion,
                        languageCode = languageCode
                    )
                    emit(ResultData.Success(summonerSpellList))
                }.catch {
                    emit(ResultData.Failed(Exception(it)))
                }
            }
}