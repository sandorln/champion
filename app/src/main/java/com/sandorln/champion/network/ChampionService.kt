package com.sandorln.champion.network

import com.sandorln.champion.model.response.LolChampionResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ChampionService {
    /* 모든 챔피언 정보 가져오기 by LanguageCode */
    @GET("/cdn/{champion_version}/data/{languageCode}/champion.json")
    suspend fun getAllChampion(
        @Path("champion_version") champVersion: String,
        @Path("languageCode") languageCode: String
    ): LolChampionResponse

    /* 특정 챔피언 정보 가져오기 by LanguageCode */
    @GET("/cdn/{champion_version}/data/{languageCode}/champion/{champion_name}.json")
    suspend fun getChampionDetailInfo(
        @Path("champion_version") champVersion: String,
        @Path("champion_name") champName: String,
        @Path("languageCode") languageCode: String
    ): LolChampionResponse
}