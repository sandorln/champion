package com.sandorln.network.service

import com.sandorln.network.BuildConfig
import com.sandorln.network.model.NetworkSummonerSpell
import com.sandorln.network.model.response.BaseLolResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SummonerSpellService @Inject constructor(
    private val ktorClient: HttpClient
) {
    suspend fun getAllSummonerSpellMap(version: String): Map<String, NetworkSummonerSpell> {
        val response = ktorClient
            .get(BuildConfig.BASE_URL + "/cdn/${version}/data/${BuildConfig.BASE_LANGUAGE}/summoner.json")
            .body<BaseLolResponse<Map<String, NetworkSummonerSpell>>>()

        return response.data ?: throw Exception("")
    }
}