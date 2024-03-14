package com.sandorln.data.repository.spell

import com.sandorln.model.data.spell.SummonerSpell
import kotlinx.coroutines.flow.Flow

interface SummonerSpellRepository {
    val currentItemList: Flow<List<SummonerSpell>>

    suspend fun refreshSummonerSpellList(version: String): Result<Any>

    suspend fun getSummonerSpellListByVersion(version: String): List<SummonerSpell>
}