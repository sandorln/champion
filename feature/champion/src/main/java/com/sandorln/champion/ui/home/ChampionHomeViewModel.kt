package com.sandorln.champion.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.data.repository.champion.ChampionRepository
import com.sandorln.data.repository.sprite.SpriteRepository
import com.sandorln.data.repository.version.VersionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class ChampionHomeViewModel @Inject constructor(
    versionRepository: VersionRepository,
    championRepository: ChampionRepository,
    spriteRepository: SpriteRepository
) : ViewModel() {
    private val _currentChampionList = championRepository.currentSummaryChampionList.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    val currentSpriteMap = spriteRepository.currentSpriteFileMap.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyMap())
    private val _championMutex = Mutex()

    private val _championUiState = MutableStateFlow(ChampionHomeUiState())
    val championUiState = _championUiState.asStateFlow()
    val displayChampionList = combine(_championUiState, _currentChampionList) { uiState, championList ->
        val searchKeyword = uiState.searchKeyword

        championList.filter { champion ->
            champion.name.startsWith(searchKeyword)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _championAction = MutableSharedFlow<ChampionHomeAction>()
    fun sendAction(championHomeAction: ChampionHomeAction) = viewModelScope.launch {
        _championAction.emit(championHomeAction)
    }

    init {
        viewModelScope.launch {
            launch {
                _championAction.collect { action ->
                    _championMutex.withLock {
                        val currentUiState = _championUiState.value
                        when (action) {
                            is ChampionHomeAction.RefreshChampionData -> {
                                /* TODO :: 챔피언 목록 / 아이콘 갱신 */
                                _championUiState.emit(currentUiState.copy(isLoading = true))
                                delay(2000)
                                _championUiState.emit(currentUiState.copy(isLoading = false))
                            }

                            is ChampionHomeAction.ChangeChampionSearchKeyword -> {
                                _championUiState.emit(currentUiState.copy(searchKeyword = action.searchKeyword))
                            }
                        }
                    }
                }
            }

            launch(Dispatchers.IO) {
                combine(
                    versionRepository.currentVersion.distinctUntilChangedBy { it.isDownLoadChampionIconSprite },
                    _currentChampionList
                ) { version, championList ->
                    if (version.isDownLoadChampionIconSprite || championList.isEmpty())
                        return@combine

                    val spriteFileList = championList.map { it.image.sprite }.distinct()
                    val spriteResult = spriteRepository.refreshSpriteBitmap(version.name, spriteFileList).isSuccess

                    val tempVersion = version.copy(isDownLoadChampionIconSprite = spriteResult)
                    versionRepository.updateVersionData(tempVersion)
                }.collect()
            }
        }
    }
}

data class ChampionHomeUiState(
    val isLoading: Boolean = false,
    val searchKeyword: String = ""
)

sealed interface ChampionHomeAction {
    data object RefreshChampionData : ChampionHomeAction
    data class ChangeChampionSearchKeyword(val searchKeyword: String) : ChampionHomeAction
}