package com.sandorln.champion.network

import retrofit2.http.GET

interface LanguageService {
    /* 사용가능한 언어 가져오기 */
    @GET("/cdn/languages.json")
    suspend fun getLanguages(): List<String>
}