package com.sandorln.champion.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sandorln.champion.model.ChampionBoard
import com.sandorln.champion.model.ChampionData
import com.sandorln.champion.model.keys.BundleKeys
import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.repository.BoardRepository
import com.sandorln.champion.repository.ChampionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChampionViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val savedStateHandle: SavedStateHandle,
    private val championRepository: ChampionRepository,
    private val boardRepository: BoardRepository
) : AndroidViewModel(context as Application) {
    private val _championAllList: Flow<ResultData<List<ChampionData>>> = championRepository.championList
    private val _searchChampionName: MutableStateFlow<String> = MutableStateFlow("")
    val searchChampionData: StateFlow<String> = _searchChampionName.asStateFlow()

    fun changeSearchChampionName(searchName: String) = viewModelScope.launch(Dispatchers.IO) { _searchChampionName.emit(searchName) }

    val showChampionList: StateFlow<ResultData<List<ChampionData>>> = _searchChampionName
        .debounce(250)
        .combineTransform(_championAllList) { search, resultAllChampions ->
            when (resultAllChampions) {
                is ResultData.Success -> {
                    val searchChampionList = resultAllChampions.data?.filter { champion ->
                        /* 검색어 / 검색 대상 공백 제거 */
                        champion.name.replace(" ", "").startsWith(search.replace(" ", ""))
                    }?.toList()

                    emit(ResultData.Success(searchChampionList))
                }
                else -> emit(resultAllChampions)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ResultData.Loading)

    fun refreshAllChampionList() = viewModelScope.launch { championRepository.refreshAllChampionList() }

    /**
     * 특정한 챔피언의 정보를 가져올 시
     */
    suspend fun getChampionDetailInfo(characterId: String): ResultData<ChampionData> = championRepository.getChampionInfo(characterId)
    val championData: LiveData<ChampionData> = savedStateHandle.getLiveData(BundleKeys.CHAMPION_DATA)
    val championBoardList: LiveData<PagingData<ChampionBoard>> = championData.switchMap {
        liveData {
            emitSource(boardRepository.getChampionBoardPagingFlow(it.id).flow.cachedIn(viewModelScope).asLiveData(Dispatchers.IO))
        }
    }
}