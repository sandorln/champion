package com.sandorln.champion.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.sandorln.champion.model.CharacterData
import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.repository.LolRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
@HiltViewModel
class ChampViewModel @Inject constructor(@ApplicationContext context: Context) : AndroidViewModel(context as Application) {
    private val lolRepository = LolRepository()

    val characterAllList: LiveData<ResultData<List<CharacterData>>> = liveData {
        emitSource(lolRepository.getResultAllChampionList().asLiveData(Dispatchers.IO))
    }

    val selectCharacter = MutableLiveData<CharacterData>().apply { value = CharacterData() }
    val searchChampName = MutableLiveData<String>().apply { value = "" }

    /**
     * 현재 가져온 값에서 검색 기능
     */


    /**
     * 특정한 챔피언의 정보를 가져올 시
     */
    suspend fun getChampionDetailInfo(characterId: String): ResultData<CharacterData> = lolRepository.getChampionInfo(characterId)
}