package com.sandorln.champion.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.sandorln.champion.model.CharacterData
import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.repository.ChampionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
@HiltViewModel
class ChampViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val championRepository: ChampionRepository
) : AndroidViewModel(context as Application) {

    val characterAllList: LiveData<ResultData<List<CharacterData>>> = liveData {
        emitSource(championRepository.getResultAllChampionList().asLiveData(Dispatchers.IO))
    }

    val selectCharacter = MutableLiveData<CharacterData>().apply { value = CharacterData() }
    val searchChampName = MutableLiveData<String>().apply { value = "" }

    /**
     * 현재 가져온 값에서 검색 기능
     */
    fun searchChampion(searchValue: String) = viewModelScope.launch(Dispatchers.IO) {

    }


    /**
     * 특정한 챔피언의 정보를 가져올 시
     */
    suspend fun getChampionDetailInfo(characterId: String): ResultData<CharacterData> = championRepository.getChampionInfo(characterId)
}