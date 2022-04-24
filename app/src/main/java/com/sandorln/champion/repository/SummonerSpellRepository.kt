package com.sandorln.champion.repository

import com.sandorln.champion.model.SummonerSpell

interface SummonerSpellRepository {
    suspend fun getSummonerSpellList(version: String): List<SummonerSpell>
}