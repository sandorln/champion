package com.sandorln.champion.network

import com.sandorln.champion.model.response.LolSummonerSpellResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface SummonerSpellService {
    @GET("/cdn/{version}/data/{languageCode}/summoner.json")
    suspend fun getAllSummonerSpell(
        @Path("version") spellVr: String,
        @Path("languageCode") languageCode: String
    ): LolSummonerSpellResponse
}