package com.sandorln.champion.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.domain.usecase.champion.GetSummaryChampionListByCurrentVersion
import com.sandorln.domain.usecase.sprite.GetCurrentVersionDistinctBySpriteType
import com.sandorln.domain.usecase.sprite.GetSpriteBitmapByCurrentVersion
import com.sandorln.domain.usecase.sprite.RefreshDownloadSpriteBitmap
import com.sandorln.model.data.image.SpriteType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class ChampionHomeViewModel @Inject constructor(
    getSummaryChampionListByCurrentVersion: GetSummaryChampionListByCurrentVersion,
    refreshDownloadSpriteBitmap: RefreshDownloadSpriteBitmap,
    getCurrentVersionDistinctBySpriteType: GetCurrentVersionDistinctBySpriteType,
    getSpriteBitmapByCurrentVersion: GetSpriteBitmapByCurrentVersion
) : ViewModel() {
    private val _championUiState = MutableStateFlow(ChampionHomeUiState())
    val championUiState = _championUiState.asStateFlow()

    private val _searchKeyword = _championUiState.map { it.searchKeyword }.distinctUntilChanged()

    private val _championAction = MutableSharedFlow<ChampionHomeAction>()
    fun sendAction(championHomeAction: ChampionHomeAction) = viewModelScope.launch {
        _championAction.emit(championHomeAction)
    }

    private val _championMutex = Mutex()
    private val _currentChampionList = getSummaryChampionListByCurrentVersion.invoke().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    val currentSpriteMap = getSpriteBitmapByCurrentVersion.invoke(SpriteType.Champion).stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyMap())

    val displayChampionList = combine(_searchKeyword, _currentChampionList) { searchKeyword, championList ->
        championList.filter { champion -> champion.name.startsWith(searchKeyword) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    init {
        viewModelScope.launch {
            launch {
                _championAction.collect { action ->
                    _championMutex.withLock {
                        val currentUiState = _championUiState.value
                        when (action) {
                            is ChampionHomeAction.RefreshChampionData -> {
                                _championUiState.emit(currentUiState.copy(isLoading = true))
                                val spriteFileList = _currentChampionList.value.map { item -> item.image.sprite }.distinct()
                                refreshDownloadSpriteBitmap
                                    .invoke(
                                        SpriteType.Champion,
                                        spriteFileList
                                    ).onFailure {
                                        /* TODO :: 오류 발생 시 처리 */
                                    }
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
                combine(getCurrentVersionDistinctBySpriteType.invoke(SpriteType.Champion), _currentChampionList) { version, championList ->
                    if (version.isDownLoadChampionIconSprite || championList.isEmpty())
                        return@combine null

                    championList.map { item -> item.image.sprite }.distinct()
                }.filterNotNull()
                    .collectLatest { spriteFileList ->
                        refreshDownloadSpriteBitmap
                            .invoke(
                                SpriteType.Champion,
                                spriteFileList
                            ).onFailure {
                                /* TODO :: 오류 발생 시 처리 */
                            }
                    }
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