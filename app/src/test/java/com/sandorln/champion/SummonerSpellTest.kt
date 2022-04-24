package com.sandorln.champion

import com.sandorln.champion.network.SummonerSpellService
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SummonerSpellTest {
    lateinit var summonerSpellService: SummonerSpellService

    @Before
    fun before() {
        summonerSpellService = Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SummonerSpellService::class.java)
    }

    @Test
    fun testSummonerSpellResponse() {
        runBlocking {
            val version = "0.151.101"
            val spellResponse = summonerSpellService.getAllSummonerSpell(version)
            spellResponse.parsingData()
            println("아이템 데이터 값 : ${spellResponse.summonerSpellList}")
        }
    }
}