package com.sandorln.champion.network

import com.sandorln.champion.model.response.LolSummonerSpellResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface SummonerSpellService {
    @GET("/cdn/{version}/data/ko_KR/summoner.json")
    suspend fun getAllSummonerSpell(@Path("version") spellVr: String): LolSummonerSpellResponse
}