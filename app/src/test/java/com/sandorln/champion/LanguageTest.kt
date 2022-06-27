package com.sandorln.champion

import com.sandorln.champion.network.LanguageService
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LanguageTest {

    lateinit var languageService: LanguageService

    @Before
    fun before() {
        languageService = Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LanguageService::class.java)
    }

    @Test
    fun 언어리스트받기() {
        runBlocking {
            val languages = languageService.getLanguages()
            println("언어 팩 : $languages")
        }
    }
}