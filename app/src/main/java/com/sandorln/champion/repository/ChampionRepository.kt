package com.sandorln.champion.repository

import com.sandorln.model.ChampionData

interface ChampionRepository {
    suspend fun getChampionList(championVersion: String, search: String, languageCode: String): List<com.sandorln.model.ChampionData>
    suspend fun getChampionInfo(championVersion: String, championId: String, languageCode: String): com.sandorln.model.ChampionData
}