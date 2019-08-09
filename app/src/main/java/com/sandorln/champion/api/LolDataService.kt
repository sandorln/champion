package com.sandorln.champion.api

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.sandorln.champion.data.CharacterData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface LolDataService {

    @GET("/cdn/6.24.1/data/ko_KR/champion.json")
    fun apiGetAllChampion(): Call<LolDataServiceResponse>

    companion object {
        private const val BASE_URL = "http://ddragon.leagueoflegends.com"
        private fun create(): LolDataService {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BASIC

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(LolDataService::class.java)
        }

        /**
         * 모든 챔피언 정보 가져오기
         */
        fun getAllChampion(onSuccess: (result: LolDataServiceResponse) -> Unit, onError: (error: String) -> Unit) {
            create().apiGetAllChampion()
                .enqueue(object : Callback<LolDataServiceResponse> {
                    override fun onResponse(
                        call: Call<LolDataServiceResponse>,
                        response: Response<LolDataServiceResponse>
                    ) {
                        if (response.body()!!.parsingData())
                            onSuccess(response.body()!!)
                        else
                            onError("Error : Not Find Data !!")
                    }

                    override fun onFailure(call: Call<LolDataServiceResponse>, t: Throwable) {
                        onError(t.message.toString())
                    }
                })
        }
    }
}