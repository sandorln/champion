package com.sandorln.champion.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sandorln.champion.api.LolDataServiceResponse
import com.sandorln.champion.data.CharacterData
import com.sandorln.champion.model.LolDataModel

class MainViewModel : ViewModel() {

    private val lolDataModel = LolDataModel()

    val characterDefaultList: LiveData<List<CharacterData>> = Transformations.map(responseData) {
        responseData.value!!.rCharacterList
    }

    val searchChamp = MutableLiveData<String>().apply { value = "" }

    val isLoading: LiveData<Boolean> get() = lolDataModel.isLoading
    private val responseData: LiveData<LolDataServiceResponse> get() = lolDataModel.successResult
    val errorMsg: LiveData<String> get() = lolDataModel.errorResult

    fun getAllChampion() {
        lolDataModel.getAllChampion()
    }
}