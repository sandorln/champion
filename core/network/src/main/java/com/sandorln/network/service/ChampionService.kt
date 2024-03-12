package com.sandorln.network.service

import com.sandorln.network.BuildConfig
import com.sandorln.network.model.champion.NetworkChampion
import com.sandorln.network.model.champion.NetworkChampionDetail
import com.sandorln.network.model.response.BaseLolResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChampionService @Inject constructor(
    private val ktorClient: HttpClient,
) {
    /**
     * 해당 버전의 간략한 챔피온 정보 가져오기
     * @return Key : 해당 챔피언 고유 Key Value
     */
    suspend fun getAllChampionDataMap(version: String): Map<String, NetworkChampion> {
        val response = ktorClient
            .get(BuildConfig.BASE_URL + "/cdn/${version}/data/${BuildConfig.BASE_LANGUAGE}/champion.json")
            .body<BaseLolResponse<Map<String, NetworkChampion>>>()

        return response.data ?: throw Exception("")
    }

    suspend fun getChampionDetail(version: String, championName: String): NetworkChampionDetail {
        val response = ktorClient
            .get(BuildConfig.BASE_URL + "/cdn/${version}/data/ko_KR/champion/${championName}.json")
            .body<BaseLolResponse<Map<String, NetworkChampionDetail>>>()

        return response.data?.get(championName) ?: throw Exception("")
    }
}