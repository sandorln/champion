package com.sandorln.champion.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sandorln.champion.api.LolApiClient
import com.sandorln.champion.api.data.CharacterData
import com.sandorln.champion.api.data.LolVersion
import com.sandorln.champion.api.response.LolDataServiceResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LolRepository {
    val isLoading = MutableLiveData<Boolean>().apply { value = false }
    val errorResult = MutableLiveData<String>().apply { value = "" }

    /**
     * 모든 챔피언 정보 가져오기
     */
    fun getAllChampion(): LiveData<List<CharacterData>> {
        isLoading.postValue(true)
        val characterList = MutableLiveData<List<CharacterData>>()

        LolApiClient
            .getService()
            .getAllChampion(LolApiClient.lolVersion!!.lvCategory.cvChampion)
            .enqueue(object : Callback<LolDataServiceResponse> {
                override fun onResponse(call: Call<LolDataServiceResponse>, response: Response<LolDataServiceResponse>) {
                    if (response.body()!!.parsingData())
                        characterList.postValue(response.body()!!.rCharacterList)
                    else
                        errorResult.postValue("Error : Not Find Data !!")

                    isLoading.postValue(false)
                }

                override fun onFailure(call: Call<LolDataServiceResponse>, t: Throwable) {
                    errorResult.postValue("Error : Network Not Connection")
                    isLoading.postValue(false)
                }
            })

        return characterList
    }

    /**
     * 특정 캐릭터 정보값 가져오기
     */
    fun getChampionInfo(champID: String, onComplete: (selectCharacter: CharacterData) -> Unit) {
        isLoading.postValue(true)

        LolApiClient
            .getService()
            .getChampionDetailInfo(LolApiClient.lolVersion!!.lvCategory.cvChampion, champID)
            .enqueue(object : Callback<LolDataServiceResponse> {
                override fun onResponse(call: Call<LolDataServiceResponse>, response: Response<LolDataServiceResponse>) {
                    if (response.body()!!.parsingData())
                        onComplete(response.body()!!.rCharacterList.first())
                    else
                        errorResult.postValue("Error : Not Find Data !!")

                    isLoading.postValue(false)
                }

                override fun onFailure(call: Call<LolDataServiceResponse>, t: Throwable) {
                    errorResult.postValue("Error : Network Not Connection")
                    isLoading.postValue(false)
                }
            })
    }

    /**
     * 버전 값 가져오기
     */
    fun getVersion(onComplete: () -> Unit) {
        LolApiClient
            .getService()
            .getVersion()
            .enqueue(object : Callback<LolVersion> {
                override fun onFailure(call: Call<LolVersion>, t: Throwable) {
                    errorResult.postValue("Error : Network Not Connection")
                }

                override fun onResponse(call: Call<LolVersion>, response: Response<LolVersion>) {
                    if (response.isSuccessful) {
                        LolApiClient.lolVersion = response.body()!!
                        onComplete()
                    } else
                        errorResult.postValue("Error : Not Find Data !!")
                }
            })
    }
}