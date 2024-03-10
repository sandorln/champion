package com.sandorln.champion.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.domain.usecase.champion.GetChampionDetail
import com.sandorln.domain.usecase.champion.HasChampionDetail
import com.sandorln.domain.usecase.version.GetAllVersionList
import com.sandorln.model.data.champion.ChampionDetailData
import com.sandorln.model.data.champion.ChampionSpell
import com.sandorln.model.keys.BundleKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class ChampionDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getChampionDetail: GetChampionDetail,
    private val hasChampionDetail: HasChampionDetail,
    private val getAllVersionList: GetAllVersionList
) : ViewModel() {
    private val _championId = savedStateHandle.get<String>(BundleKeys.CHAMPION_ID) ?: ""
    private val _version = savedStateHandle.getStateFlow(BundleKeys.CHAMPION_VERSION, "")

    private val _uiMutex = Mutex()
    private val _uiState = MutableStateFlow(ChampionDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val _action = MutableSharedFlow<ChampionDetailAction>()
    fun sendAction(action: ChampionDetailAction) = viewModelScope.launch {
        _action.emit(action)
    }

    private val _sideEffect = MutableSharedFlow<ChampionDetailSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    init {
        viewModelScope.launch {
            launch {
                getAllVersionList
                    .invoke()
                    .map { versionList ->
                        versionList.map { version ->
                            version.name
                        }
                    }
                    .distinctUntilChanged()
                    .collectLatest { versionList ->
                        _uiMutex.withLock {
                            _uiState.update {
                                it.copy(
                                    versionNameList = versionList,
                                )
                            }
                        }
                    }
            }
            launch {
                _version.collectLatest { version ->
                    _uiMutex.withLock {
                        _uiState.update {
                            it.copy(
                                isShowVersionListDialog = false,
                                selectedVersion = version,
                            )
                        }
                    }

                    getChampionDetail
                        .invoke(_championId, version)
                        .onSuccess { championDetailData ->
                            _uiMutex.withLock {
                                _uiState.update {
                                    it.copy(
                                        championDetailData = championDetailData,
                                        selectedSkill = championDetailData.passive
                                    )
                                }
                            }
                        }.onFailure {
                            _sideEffect.emit(ChampionDetailSideEffect.ShowToastMessage(it.message ?: "Error"))
                        }
                }
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

                        is ChampionDetailAction.ChangeVersion -> {
                            hasChampionDetail.invoke(
                                championId = _championId,
                                version = action.versionName
                            ).onSuccess { hasData ->
                                if (hasData) {
                                    savedStateHandle[BundleKeys.CHAMPION_VERSION] = action.versionName
                                    _uiMutex.withLock {
                                        _uiState.update {
                                            it.copy(isShowVersionListDialog = false)
                                        }
                                    }
                                } else {
                                    _sideEffect.emit(ChampionDetailSideEffect.ShowToastMessage("해당 버전 때 챔피언이 없었습니다"))
                                }
                            }.onFailure {
                                _sideEffect.emit(ChampionDetailSideEffect.ShowToastMessage(it.message ?: "Error"))
                            }
                        }

                        is ChampionDetailAction.ChangeVersionListDialog -> {
                            _uiMutex.withLock {
                                _uiState.update {
                                    it.copy(
                                        isShowVersionListDialog = action.visible
                                    )
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
    val selectedVersion: String = "",
    val selectedSkill: ChampionSpell = ChampionSpell(),
    val versionNameList: List<String> = listOf(),
    val isShowVersionListDialog: Boolean = false
)

sealed interface ChampionDetailAction {
    data class ChangeVersion(val versionName: String) : ChampionDetailAction
    data class ChangeSelectSkill(val skill: ChampionSpell) : ChampionDetailAction
    data class ChangeVersionListDialog(val visible: Boolean) : ChampionDetailAction
}

sealed interface ChampionDetailSideEffect {
    data class ShowToastMessage(val message: String) : ChampionDetailSideEffect
}
