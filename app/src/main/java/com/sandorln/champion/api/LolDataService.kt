package com.sandorln.champion.api

import com.sandorln.champion.api.data.LolVersion
import com.sandorln.champion.api.response.LolDataServiceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface LolDataService {

    /* 모든 챔피언 정보 가져오기 */
    @GET("/cdn/{champion_version}/data/ko_KR/champion.json")
    fun getAllChampion(@Path("champion_version") champVersion: String): Call<LolDataServiceResponse>

    /* 특정 챔피언 정보 가져오기 */
    @GET("/cdn/{champion_version}/data/ko_KR/champion/{champion_name}.json")
    fun apiGetChampionInfo(@Path("champion_version") champVersion: String, @Path("champion_name") champName: String): Call<LolDataServiceResponse>

    @GET("/realms/na.json")
    fun getVersion(): Call<LolVersion>

}