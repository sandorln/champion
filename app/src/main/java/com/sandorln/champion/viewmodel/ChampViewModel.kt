package com.sandorln.champion.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sandorln.champion.api.data.CharacterData
import com.sandorln.champion.repository.LolRepository

class ChampViewModel(application: Application) : AndroidViewModel(application) {
    private val lolRepository = LolRepository()

    val characterAllList: LiveData<List<CharacterData>> = lolRepository.getAllChampion()
    val selectCharacter = MutableLiveData<CharacterData>().apply { value = CharacterData() }

    val searchChampName = MutableLiveData<String>().apply { value = "" }

    val isLoading: LiveData<Boolean> = lolRepository.isLoading
    val errorMsg: LiveData<String> = lolRepository.errorResult



    fun getChampionInfo(characterData: CharacterData) {
        lolRepository.getChampionInfo(characterData.cId) {
            selectCharacter.postValue(it)
        }
    }

    fun getVersion(onComplete: () -> Unit) {
        lolRepository.getVersion(onComplete)
    }
}