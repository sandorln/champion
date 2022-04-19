package com.sandorln.champion.repository

import com.sandorln.champion.model.ChampionData
import com.sandorln.champion.model.result.ResultData
import kotlinx.coroutines.flow.Flow

interface ChampionRepository {
    fun getChampionList(championVersion: String, search: String): Flow<ResultData<List<ChampionData>>>
    fun getChampionInfo(championVersion: String,championId : String) : Flow<ResultData<ChampionData>>
}