package com.sandorln.champion.repository

import com.sandorln.model.SummonerSpell

interface SummonerSpellRepository {
    suspend fun getSummonerSpellList(summonerSpellVersion: String, languageCode : String): List<com.sandorln.model.SummonerSpell>
}