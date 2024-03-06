package com.sandorln.champion.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.domain.usecase.champion.GetSummaryChampionListByCurrentVersion
import com.sandorln.domain.usecase.sprite.GetCurrentVersionDistinctBySpriteType
import com.sandorln.domain.usecase.sprite.GetSpriteBitmapByCurrentVersion
import com.sandorln.domain.usecase.sprite.RefreshDownloadSpriteBitmap
import com.sandorln.domain.usecase.version.GetCurrentVersion
import com.sandorln.model.data.image.SpriteType
import com.sandorln.model.type.ChampionTag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class ChampionHomeViewModel @Inject constructor(
    getSummaryChampionListByCurrentVersion: GetSummaryChampionListByCurrentVersion,
    refreshDownloadSpriteBitmap: RefreshDownloadSpriteBitmap,
    getCurrentVersionDistinctBySpriteType: GetCurrentVersionDistinctBySpriteType,
    getSpriteBitmapByCurrentVersion: GetSpriteBitmapByCurrentVersion,
    getCurrentVersion: GetCurrentVersion
) : ViewModel() {
    val currentVersion = getCurrentVersion
        .invoke()
        .map { it.name }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")
    private val _championUiState = MutableStateFlow(ChampionHomeUiState())
    val championUiState = _championUiState.asStateFlow()

    private val _championAction = MutableSharedFlow<ChampionHomeAction>()
    fun sendAction(championHomeAction: ChampionHomeAction) = viewModelScope.launch {
        _championAction.emit(championHomeAction)
    }

    private val _championMutex = Mutex()
    private val _currentChampionList = getSummaryChampionListByCurrentVersion.invoke().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    val currentSpriteMap = getSpriteBitmapByCurrentVersion.invoke(SpriteType.Champion).stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyMap())

    val displayChampionList = combine(_championUiState, _currentChampionList) { uiState, championList ->
        val searchKeyword = uiState.searchKeyword
        val tagFilterSet = uiState.selectChampionTagSet

        if (searchKeyword.trim().isEmpty() && tagFilterSet.isEmpty())
            return@combine championList

        championList.filter { champion ->
            val isMatchKeyword = if (searchKeyword.isNotEmpty()) {
                champion.name.startsWith(searchKeyword)
            } else {
                true
            }

            val isMatchTagFilter = champion.tags.containsAll(tagFilterSet)

            isMatchKeyword && isMatchTagFilter
        }
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

                            is ChampionHomeAction.ToggleChampionTag -> {
                                val championTag = action.championTag
                                val tempChampionTagSet = currentUiState
                                    .selectChampionTagSet
                                    .toMutableSet()
                                    .apply {
                                        if (contains(championTag)) {
                                            remove(championTag)
                                        } else {
                                            add(championTag)
                                        }
                                    }

                                _championUiState.update {
                                    it.copy(
                                        selectChampionTagSet = tempChampionTagSet
                                    )
                                }
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
    val searchKeyword: String = "",
    val selectChampionTagSet: Set<ChampionTag> = setOf()
)

sealed interface ChampionHomeAction {
    data object RefreshChampionData : ChampionHomeAction

    data class ChangeChampionSearchKeyword(val searchKeyword: String) : ChampionHomeAction
    data class ToggleChampionTag(val championTag: ChampionTag) : ChampionHomeAction
}