package com.sandorln.champion.ui.home

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.domain.usecase.champion.GetChampionPatchNoteList
import com.sandorln.domain.usecase.champion.GetSummaryChampionListByCurrentVersion
import com.sandorln.domain.usecase.sprite.GetCurrentVersionDistinctBySpriteType
import com.sandorln.domain.usecase.sprite.GetSpriteBitmapByCurrentVersion
import com.sandorln.domain.usecase.sprite.RefreshDownloadSpriteBitmap
import com.sandorln.domain.usecase.version.GetCurrentVersion
import com.sandorln.model.data.champion.SummaryChampion
import com.sandorln.model.data.image.SpriteType
import com.sandorln.model.data.patchnote.PatchNoteData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChampionHomeViewModel @Inject constructor(
    getCurrentVersion: GetCurrentVersion,
    getSummaryChampionListByCurrentVersion: GetSummaryChampionListByCurrentVersion,
    getCurrentVersionDistinctBySpriteType: GetCurrentVersionDistinctBySpriteType,
    getSpriteBitmapByCurrentVersion: GetSpriteBitmapByCurrentVersion,
    private val refreshDownloadSpriteBitmap: RefreshDownloadSpriteBitmap,
    private val getChampionPatchNoteList: GetChampionPatchNoteList
) : ViewModel() {
    private val _championUiState = MutableStateFlow(ChampionHomeUiState())
    val championUiState = _championUiState.asStateFlow()

    private var _latestAllSummaryChampionList: List<SummaryChampion> = listOf()
    private val _searchKeyword = MutableStateFlow("")
    private val _span = MutableStateFlow(1)

    fun sendAction(action: ChampionHomeAction) {
        when (action) {
            is ChampionHomeAction.RefreshChampionData -> refreshChampionData()
            is ChampionHomeAction.ChangeChampionSearchKeyword -> _searchKeyword.update { action.searchKeyword }
            is ChampionHomeAction.ChangeSpan -> _span.update { action.span }
        }
    }

    private val _sideEffect = MutableSharedFlow<ChampionHomeSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    // TODO :: 버전 변경 시 패치 노트가 여러번 호출 됨
    private var _refreshJob: Job? = null
    private fun refreshChampionData() {
        _refreshJob?.cancel()

        _refreshJob = viewModelScope.launch {
            val currentVersion = _championUiState.value.currentVersionName
            _championUiState.update {
                it.copy(
                    isLoading = true,
                    championPatchNoteList = null
                )
            }

            val spriteFileList = _latestAllSummaryChampionList.map { item -> item.image.sprite }
            refreshDownloadSpriteBitmap
                .invoke(
                    SpriteType.Champion,
                    spriteFileList
                ).onFailure {
                    _sideEffect.emit(ChampionHomeSideEffect.ShowErrorMessage(it as Exception))
                }

            val championPatchNoteList = getChampionPatchNoteList.invoke(currentVersion).getOrNull() ?: emptyList<PatchNoteData>()
            _championUiState.update {
                it.copy(
                    isLoading = false,
                    championPatchNoteList = championPatchNoteList
                )
            }
        }
    }

    init {
        viewModelScope.launch {
            /**  다운로드가 되지 않은 Sprite 가 있을 시 다시 다운로드 시작 */
            launch(Dispatchers.IO) {
                combine(
                    getCurrentVersionDistinctBySpriteType.invoke(SpriteType.Champion),
                    getSummaryChampionListByCurrentVersion.invoke()
                ) { version, championList ->
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
                                _sideEffect.emit(ChampionHomeSideEffect.ShowErrorMessage(it as Exception))
                            }
                    }
            }

            launch {
                getSpriteBitmapByCurrentVersion
                    .invoke(SpriteType.Champion)
                    .collectLatest { currentSpriteMap ->
                        _championUiState.update {
                            it.copy(currentSpriteMap = currentSpriteMap)
                        }
                    }
            }

            launch {
                combine(
                    getSummaryChampionListByCurrentVersion.invoke(),
                    _span,
                    _searchKeyword
                ) { allChampionList, span, searchKeyword ->
                    _latestAllSummaryChampionList = allChampionList

                    val filterPassChampionList = if (searchKeyword.trim().isEmpty())
                        allChampionList
                    else
                        allChampionList.filter { champion -> champion.name.startsWith(searchKeyword) }

                    filterPassChampionList.chunked(span)
                }.collectLatest { displayChampionList ->
                    _championUiState.update {
                        it.copy(displayChampionList = displayChampionList)
                    }
                }
            }

            launch {
                getCurrentVersion
                    .invoke()
                    .collectLatest { version ->
                        _championUiState.update {
                            it.copy(
                                currentVersionName = version.name,
                                championPatchNoteList = null
                            )
                        }

                        val championPatchNoteList = getChampionPatchNoteList
                            .invoke(version.name)
                            .getOrNull() ?: emptyList()

                        _championUiState.update {
                            it.copy(championPatchNoteList = championPatchNoteList)
                        }
                    }
            }
        }
    }
}

data class ChampionHomeUiState(
    val isLoading: Boolean = false,
    val championPatchNoteList: List<PatchNoteData>? = null,
    val currentVersionName: String = "",
    val displayChampionList: List<List<SummaryChampion>> = listOf(),
    val currentSpriteMap: Map<String, Bitmap> = emptyMap()
)

sealed interface ChampionHomeAction {
    data object RefreshChampionData : ChampionHomeAction

    data class ChangeChampionSearchKeyword(val searchKeyword: String) : ChampionHomeAction
    data class ChangeSpan(val span: Int) : ChampionHomeAction
}

sealed interface ChampionHomeSideEffect {
    data class ShowErrorMessage(val exception: Exception) : ChampionHomeSideEffect
}