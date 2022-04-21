package com.sandorln.champion.use_case

import com.sandorln.champion.model.SummonerSpell
import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.repository.SummonerSpellRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest

class GetSummonerSpellList(
    private val getVersionCategory: GetVersionCategory,
    private val summonerSpellRepository: SummonerSpellRepository
) {
    operator fun invoke(): Flow<ResultData<List<SummonerSpell>>> {
        return getVersionCategory()
            .mapLatest { it.summoner }
            .flatMapLatest { summonerVersion ->
                if (summonerVersion.isEmpty())
                    flow { emit(ResultData.Failed(Exception("버전 정보를 알 수 없습니다"))) }
                else
                    summonerSpellRepository.getSummonerSpellList(summonerVersion)
            }
    }
}