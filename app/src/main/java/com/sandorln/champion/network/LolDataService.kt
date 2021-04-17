package com.sandorln.champion.network

import com.sandorln.champion.model.LolVersion
import com.sandorln.champion.network.response.LolDataServiceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface LolDataService {

    /* 모든 챔피언 정보 가져오기 */
    @GET("/cdn/{champion_version}/data/ko_KR/champion.json")
    suspend fun getAllChampion(@Path("champion_version") champVersion: String): LolDataServiceResponse

    /* 특정 챔피언 정보 가져오기 */
    @GET("/cdn/{champion_version}/data/ko_KR/champion/{champion_name}.json")
    suspend fun getChampionDetailInfo(@Path("champion_version") champVersion: String, @Path("champion_name") champName: String): LolDataServiceResponse

    @GET("/realms/na.json")
    suspend fun getVersion(): LolVersion

}