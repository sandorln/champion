package com.sandorln.champion.repository

import com.sandorln.champion.model.ChampionData
import com.sandorln.champion.model.result.ResultData
import kotlinx.coroutines.flow.Flow

interface ChampionRepository {
    suspend fun refreshAllChampionList(championVersion: String)
    fun getAllChampionListFlow(championVersion: String): Flow<List<ChampionData>>

    suspend fun getChampionInfo(champID: String, version: String): ResultData<ChampionData>
}