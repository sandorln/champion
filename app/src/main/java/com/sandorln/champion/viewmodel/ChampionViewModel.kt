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
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class ChampionViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val savedStateHandle: SavedStateHandle,
    private val championRepository: ChampionRepository,
    private val boardRepository: BoardRepository
) : AndroidViewModel(context as Application) {
    private val championAllList: Flow<ResultData<List<ChampionData>>> = championRepository.championList
    private val searchChampionName: MutableStateFlow<String> = MutableStateFlow("")
    val showChampionList: MutableStateFlow<ResultData<List<ChampionData>>> = MutableStateFlow(ResultData.Loading)

    fun changeSearchChampionName(searchName: String) = viewModelScope.launch(Dispatchers.IO) { searchChampionName.emit(searchName) }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            delay(500)
            championRepository.refreshAllChampionList()

            championAllList.combine(searchChampionName) { championAllList, searchChampionName ->
                val searchResult = when {
                    /* 로딩 중일 시 */
                    championAllList is ResultData.Loading -> ResultData.Loading

                    /* 검색어가 없을 시 모든 챔피언 보여주기 */
                    championAllList is ResultData.Success && searchChampionName.isEmpty() -> ResultData.Success(championAllList.data)

                    /* 검색어가 있을 시 해당 챔피언 보여주기 */
                    championAllList is ResultData.Success && searchChampionName.isNotEmpty() -> {
                        val searchChampionList = championAllList.data?.filter { champion ->
                            /* 검색어 / 검색 대상 공백 제거 */
                            champion.cName.replace(" ", "").startsWith(searchChampionName.replace(" ", ""))
                        }?.toList()
                        ResultData.Success(searchChampionList)
                    }

                    else -> ResultData.Failed(Exception())
                }
                showChampionList.emit(searchResult)
            }.collect()
        }
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