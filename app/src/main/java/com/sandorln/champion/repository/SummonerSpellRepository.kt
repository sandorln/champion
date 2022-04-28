package com.sandorln.champion.repository

import com.sandorln.champion.model.SummonerSpell

interface SummonerSpellRepository {
    suspend fun getSummonerSpellList(summonerSpellVersion: String): List<SummonerSpell>
}