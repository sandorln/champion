package com.sandorln.champion.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sandorln.model.ChampionData
import com.sandorln.model.keys.BundleKeys
import com.sandorln.model.result.ResultData
import com.sandorln.champion.usecase.GetAppSettingUseCase
import com.sandorln.champion.usecase.GetChampionInfoUseCase
import com.sandorln.champion.usecase.GetChampionListUseCase
import com.sandorln.champion.usecase.GetChampionVersionUseCase
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
    private val getChampionVersionUseCase: GetChampionVersionUseCase,
    private val getChampionListUseCase: GetChampionListUseCase,
    private val getChampionInfoUseCase: GetChampionInfoUseCase,
    private val getAppSettingUseCase: GetAppSettingUseCase
) : AndroidViewModel(context as Application) {
    private val _searchChampionName: MutableStateFlow<String> = MutableStateFlow("")
    fun changeSearchChampionName(searchName: String) = viewModelScope.launch(Dispatchers.IO) { _searchChampionName.emit(searchName) }

    private val _showChampionList: MutableStateFlow<com.sandorln.model.result.ResultData<List<ChampionData>>> = MutableStateFlow(ResultData.Loading)
    val showChampionList = _showChampionList
        .onStart {
            when (val result = _showChampionList.firstOrNull()) {
                is ResultData.Success -> {
                    result.data?.let { championList ->
                        /* 현재 보여지고 있는 챔피언 버전과 설정에서 설정된 버전이 다를 시 갱신 */
                        val nowShowChampionVersion = championList.firstOrNull()?.version ?: ""
                        val localChampionVersion = getChampionVersionUseCase().first()
                        if (nowShowChampionVersion != localChampionVersion)
                            refreshChampionList()
                    }
                }
                /* 오류 상태였을 경우 곧바로 갱신 */
                is ResultData.Failed -> refreshChampionList()
                else -> {}
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ResultData.Loading)

    fun refreshChampionList() = viewModelScope.launch(Dispatchers.IO) { _showChampionList.emitAll(getChampionListUseCase(_searchChampionName.value)) }

    fun getChampionDetailInfo(championVersion: String, championId: String) = getChampionInfoUseCase(championVersion, championId)
    val championData: LiveData<ChampionData> = savedStateHandle.getLiveData(com.sandorln.model.keys.BundleKeys.CHAMPION_DATA, ChampionData())

    init {
        viewModelScope.launch {
            _searchChampionName
                .transform { search -> emitAll(getChampionListUseCase(search)) }
                .collectLatest { _showChampionList.emit(it) }
        }
    }
}