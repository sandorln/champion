package com.sandorln.data.repository.champion

import com.sandorln.model.data.champion.ChampionDetailData
import com.sandorln.model.data.champion.SummaryChampion
import kotlinx.coroutines.flow.Flow

interface ChampionRepository {
    val currentSummaryChampionList: Flow<List<SummaryChampion>>

    suspend fun refreshChampionList(version: String): Result<Any>

    suspend fun getSummaryChampionListByVersion(version: String): List<SummaryChampion>
    suspend fun getSummaryChampionListByChampionIdList(version: String, championIdList: List<String>): List<SummaryChampion>

    suspend fun getNewChampionIdList(versionName: String, preVersionName: String): List<String>

    suspend fun getSummaryChampion(championId: String, version: String): SummaryChampion?
    suspend fun getChampionDetail(championId: String, version: String): ChampionDetailData
    suspend fun hasChampionDetailData(championId: String, version: String): Boolean
}