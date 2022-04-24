package com.sandorln.champion.usecase

import com.sandorln.champion.model.ChampionData
import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.repository.ChampionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

class GetChampionList(
    private val getVersion: GetVersion,
    private val championRepository: ChampionRepository
) {
    operator fun invoke(search: String): Flow<ResultData<List<ChampionData>>> =
        getVersion()
            .flatMapLatest { totalVersion ->
                flow {
                    if (totalVersion.isEmpty())
                        throw Exception("버전 정보가 없습니다.")

                    emit(ResultData.Loading)
                    val championList = championRepository.getChampionList(totalVersion, search)
                    emit(ResultData.Success(championList))
                }.catch {
                    emit(ResultData.Failed(Exception(it)))
                }
            }
}