package com.sandorln.champion.repository

import com.sandorln.champion.model.SummonerSpell
import com.sandorln.champion.model.result.ResultData
import kotlinx.coroutines.flow.Flow

interface SummonerSpellRepository {
    fun getSummonerSpellList(version: String): Flow<ResultData<List<SummonerSpell>>>
}