package com.sandorln.champion.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sandorln.champion.manager.VersionManager
import com.sandorln.champion.model.ChampionBoard
import com.sandorln.champion.model.ChampionData
import com.sandorln.champion.model.VersionLol
import com.sandorln.champion.model.keys.BundleKeys
import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.repository.BoardRepository
import com.sandorln.champion.repository.ChampionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
@HiltViewModel
class ChampionViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val savedStateHandle: SavedStateHandle,
    private val championRepository: ChampionRepository,
    private val boardRepository: BoardRepository
) : AndroidViewModel(context as Application) {
    val championAllList: LiveData<ResultData<List<ChampionData>>> = liveData {
        emitSource(championRepository.getResultAllChampionList().asLiveData(Dispatchers.IO))
    }

    /**
     * 현재 가져온 값에서 검색 기능
     */
    val searchChampName = MutableLiveData<String>().apply { value = "" }

    fun searchChampion(searchChampionName: String) = viewModelScope.launch(Dispatchers.IO) {
        championRepository.searchChampion(searchChampionName)
    }

    /**
     * 특정한 챔피언의 정보를 가져올 시
     */
    suspend fun getChampionDetailInfo(characterId: String): ResultData<ChampionData> = championRepository.getChampionInfo(characterId)
    val championData: LiveData<ChampionData> = savedStateHandle.getLiveData(BundleKeys.CHAMPION_DATA)
    val championBoardList: LiveData<PagingData<ChampionBoard>> = championData.switchMap {
        liveData {
            emitSource(boardRepository.getChampionBoardPagingFlow(it.cId).flow.cachedIn(viewModelScope).asLiveData(Dispatchers.IO))
        }
    }
}