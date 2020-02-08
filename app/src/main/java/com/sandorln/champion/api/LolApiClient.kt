package com.sandorln.champion.api

import com.sandorln.champion.api.data.LolVersion
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object LolApiClient {
    var lolDataService: LolDataService? = null

    fun getService(): LolDataService {
        if (lolDataService == null) {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BASIC

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            lolDataService = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(LolDataService::class.java)
        }

        return lolDataService!!
    }

    private const val BASE_URL = "http://ddragon.leagueoflegends.com"
    var lolVersion: LolVersion? = null
}