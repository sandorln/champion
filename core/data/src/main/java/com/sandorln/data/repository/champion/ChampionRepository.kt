package com.sandorln.data.repository.champion

import com.sandorln.model.data.champion.SummaryChampion
import kotlinx.coroutines.flow.Flow

interface ChampionRepository {
    val currentSummaryChampionList: Flow<List<SummaryChampion>>

    suspend fun refreshChampionList(version: String): Result<Any>

    suspend fun getSummaryChampionListByVersion(version: String): List<SummaryChampion>
}