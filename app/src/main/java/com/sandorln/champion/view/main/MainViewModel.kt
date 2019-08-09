package com.sandorln.champion.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sandorln.champion.api.LolDataServiceResponse
import com.sandorln.champion.data.CharacterData
import com.sandorln.champion.model.LolDataModel

class MainViewModel : ViewModel() {

    val lolDataModel = LolDataModel()

    val characterList: LiveData<List<CharacterData>> = Transformations.map(responseData) {
        responseData.value!!.rCharacterList
    }

    val isLoading: LiveData<Boolean> get() = lolDataModel.isLoading
    val responseData: LiveData<LolDataServiceResponse> get() = lolDataModel.successResult
    val errorMsg: LiveData<String> get() = lolDataModel.errorResult

    fun getAllChampion() {
        lolDataModel.getAllChampion()
    }
}