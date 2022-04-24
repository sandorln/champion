package com.sandorln.champion.repository

import com.sandorln.champion.model.ChampionData

interface ChampionRepository {
    suspend fun getChampionList(totalVersion: String, search: String): List<ChampionData>
    suspend fun getChampionInfo(championVersion: String, championId: String): ChampionData
}