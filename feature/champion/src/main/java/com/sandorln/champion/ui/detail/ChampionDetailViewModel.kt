package com.sandorln.champion.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.domain.usecase.champion.GetChampionDetail
import com.sandorln.model.data.champion.ChampionDetailData
import com.sandorln.model.data.champion.ChampionSpell
import com.sandorln.model.keys.BundleKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
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

    private val _action = MutableSharedFlow<ChampionDetailAction>()
    fun sendAction(action: ChampionDetailAction) = viewModelScope.launch {
        _action.emit(action)
    }

    private suspend fun refreshChampionDetail() {
        getChampionDetail
            .invoke(_championId, _version)
            .onSuccess { championDetailData ->
                _uiMutex.withLock {
                    _uiState.update {
                        it.copy(
                            championDetailData = championDetailData,
                            selectedSkill = championDetailData.passive
                        )
                    }
                }
            }
    }

    init {
        viewModelScope.launch {
            launch {
                _uiMutex.withLock {
                    _uiState.update {
                        it.copy(version = _version)
                    }
                }
            }

            launch {
                refreshChampionDetail()
            }

            launch {
                _action.collect { action ->
                    when (action) {
                        is ChampionDetailAction.ChangeSelectSkill -> {
                            _uiMutex.withLock {
                                _uiState.update {
                                    it.copy(selectedSkill = action.skill)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class ChampionDetailUiState(
    val championDetailData: ChampionDetailData = ChampionDetailData(),
    val version: String = "",
    val selectedSkill: ChampionSpell = ChampionSpell()
)

sealed interface ChampionDetailAction {
    data class ChangeSelectSkill(val skill: ChampionSpell) : ChampionDetailAction
}

