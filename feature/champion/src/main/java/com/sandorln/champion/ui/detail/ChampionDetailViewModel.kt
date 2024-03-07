package com.sandorln.champion.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.domain.usecase.champion.GetChampionDetail
import com.sandorln.model.data.champion.ChampionDetailData
import com.sandorln.model.keys.BundleKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class ChampionDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getChampionDetail: GetChampionDetail
) : ViewModel() {
    private val _championId = savedStateHandle.get<String>(BundleKeys.CHAMPION_ID) ?: ""
    private val _version = savedStateHandle.get<String>(BundleKeys.CHAMPION_VERSION) ?: ""

    private val _uiMutex = Mutex()
    private val _uiState = MutableStateFlow(ChampionDetailUiState())
    val uiState = _uiState.asStateFlow()

    private suspend fun refreshChampionDetail() {
        getChampionDetail
            .invoke(_championId, _version)
            .onSuccess { championDetailData ->
                _uiMutex.withLock {
                    _uiState.update {
                        it.copy(championDetailData = ChampionDetailData(version = _version, id = _championId))
                    }
                }
            }
    }

    init {
        viewModelScope.launch {
            launch {
                refreshChampionDetail()
            }
        }
    }
}

data class ChampionDetailUiState(
    val championDetailData: ChampionDetailData = ChampionDetailData()
)


