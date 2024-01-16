package com.sandorln.data.repo.champion

import com.sandorln.model.data.champion.SummaryChampion
import kotlinx.coroutines.flow.Flow

interface ChampionRepository {
    val currentSummaryChampionList: Flow<List<SummaryChampion>>

    suspend fun refreshChampionList(version: String): Result<Any>
}