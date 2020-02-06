package com.sandorln.champion.api

import com.sandorln.champion.data.CharacterData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface LolDataService {

    /* 모든 챔피언 정보 가져오기 */
    @GET("/cdn/{champion_version}/data/ko_KR/champion.json")
    fun apiGetAllChampion(@Path("champion_version") champVersion: String): Call<LolDataServiceResponse>

    /* 특정 챔피언 정보 가져오기 */
    @GET("/cdn/{champion_version}/data/ko_KR/champion/{champion_name}.json")
    fun apiGetChampionInfo(@Path("champion_version") champVersion: String, @Path("champion_name") champName: String): Call<LolDataServiceResponse>


    @GET("/realms/na.json")
    fun apiGetVersion(): Call<LolVersion>

    companion object {
        private const val BASE_URL = "http://ddragon.leagueoflegends.com"
        var lolVersion: LolVersion? = null

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
            /* Game Version 을 정상적으로 받아왔을 경우 */
            if (lolVersion != null) {
                create().apiGetAllChampion(lolVersion!!.lvCategory.cvChampion)
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
            } else {
                /* Game Version 을 못받아왔을 경우 다시 받아와서 처리 */
                getGameVersion({
                    getAllChampion(onSuccess, onError)
                }, {
                    onError("게임 버전 정보를 찾을 수 없습니다")
                })
            }
        }

        /**
         * 특정 캐릭터 정보값 가져오기
         */
        fun getChampionInfo(champName : String, onSuccess: (result: CharacterData) -> Unit, onError: (error: String) -> Unit){
            /* Game Version 을 정상적으로 받아왔을 경우 */
            if (lolVersion != null) {
                create().apiGetChampionInfo(lolVersion!!.lvCategory.cvChampion, champName)
                    .enqueue(object : Callback<LolDataServiceResponse> {
                        override fun onResponse(
                            call: Call<LolDataServiceResponse>,
                            response: Response<LolDataServiceResponse>
                        ) {
                            if (response.body()!!.parsingData())
                                onSuccess(response.body()!!.rCharacterList.first())
                            else
                                onError("Error : Not Find Data !!")
                        }

                        override fun onFailure(call: Call<LolDataServiceResponse>, t: Throwable) {
                            onError(t.message.toString())
                        }
                    })
            } else {
                /* Game Version 을 못받아왔을 경우 다시 받아와서 처리 */
                getGameVersion({
                    getChampionInfo(champName, onSuccess, onError)
                }, {
                    onError("게임 버전 정보를 찾을 수 없습니다")
                })
            }
        }


        /**
         * version 정보 가져오기
         */
        fun getGameVersion(onSuccess: () -> Unit, onError: (error: String) -> Unit) {
            create().apiGetVersion()
                .enqueue(object : Callback<LolVersion> {
                    override fun onResponse(call: Call<LolVersion>, response: Response<LolVersion>) {
                        lolVersion = response.body()
                        onSuccess()
                    }

                    override fun onFailure(call: Call<LolVersion>, t: Throwable) {
                        onError("게임 버전 정보를 찾을 수 없습니다")
                    }
                })
        }
    }
}