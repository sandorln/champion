package com.sandorln.champion.usecase

import com.sandorln.champion.model.ChampionData
import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.repository.ChampionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest

class GetChampionList(
    private val getVersion: GetVersion,
    private val championRepository: ChampionRepository
) {
    operator fun invoke(search: String): Flow<ResultData<List<ChampionData>>> =
        getVersion()
            .flatMapLatest { championVersion ->
                if (championVersion.isEmpty())
                    flow { emit(ResultData.Failed(Exception("챔피언 버전 정보가 없습니다"))) }
                else
                    championRepository.getChampionList(championVersion, search)
            }
}