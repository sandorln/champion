package com.sandorln.champion.model

import androidx.lifecycle.MutableLiveData
import com.sandorln.champion.api.LolDataService
import com.sandorln.champion.api.LolDataServiceResponse

class LolDataModel {

    val isLoading = MutableLiveData<Boolean>().apply { value = false }

    val successResult = MutableLiveData<LolDataServiceResponse>().apply { value = LolDataServiceResponse() }
    val errorResult = MutableLiveData<String>().apply { value = "" }

    fun getAllChampion() {
        isLoading.postValue(true)
        LolDataService.getAllChampion({ successData ->
            successResult.postValue(successData)
            isLoading.postValue(false)
        }, { errorMsg ->
            errorResult.postValue(errorMsg)
            isLoading.postValue(false)
        })
    }
}