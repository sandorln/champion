package com.sandorln.spell.ui

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.domain.usecase.spell.GetSpellListByCurrentVersion
import com.sandorln.domain.usecase.sprite.GetCurrentVersionDistinctBySpriteType
import com.sandorln.domain.usecase.sprite.GetSpriteBitmapByCurrentVersion
import com.sandorln.domain.usecase.sprite.RefreshDownloadSpriteBitmap
import com.sandorln.domain.usecase.version.GetCurrentVersion
import com.sandorln.model.data.image.SpriteType
import com.sandorln.model.data.spell.SummonerSpell
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpellHomeViewModel @Inject constructor(
    getSpellListByCurrentVersion: GetSpellListByCurrentVersion,
    getCurrentVersionDistinctBySpriteType: GetCurrentVersionDistinctBySpriteType,
    getSpriteBitmapByCurrentVersion: GetSpriteBitmapByCurrentVersion,
    getCurrentVersion: GetCurrentVersion,
    private val refreshDownloadSpriteBitmap: RefreshDownloadSpriteBitmap,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SpellHomeUiState())
    val uiState = _uiState.asStateFlow()

    private var _latestSpellList: List<SummonerSpell> = listOf()
    private val _span = MutableStateFlow(1)

    fun sendAction(action: SpellHomeAction) {
        when (action) {
            SpellHomeAction.RefreshSpellData -> refreshSpell()

            is SpellHomeAction.SelectSpell -> _uiState.update {
                val selectedSpell = action.spell.takeIf { selectSpell ->
                    _uiState.value.selectedSpell?.id != selectSpell.id
                }
                it.copy(selectedSpell = selectedSpell)
            }

            is SpellHomeAction.ChangeSpan -> _span.update { action.span }
        }
    }

    private val _sideEffect = MutableSharedFlow<SpellHomeSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    private var _refreshJob: Job? = null
    private fun refreshSpell() {
        _refreshJob?.cancel()

        _refreshJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val spriteFileList = _latestSpellList.map { spell -> spell.image.sprite }.distinct()
            refreshDownloadSpriteBitmap
                .invoke(SpriteType.Spell, spriteFileList)
                .onFailure {
                    _sideEffect.emit(SpellHomeSideEffect.ShowErrorMessage(it as Exception))
                }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    init {
        viewModelScope.launch {
            launch {
                combine(
                    getSpellListByCurrentVersion.invoke(),
                    _span
                ) { currentSpellList, span ->
                    _latestSpellList = currentSpellList
                    currentSpellList.chunked(span)
                }.collectLatest { spellList ->
                    _uiState.update { it.copy(displaySpellList = spellList) }
                }
            }

            launch {
                getSpriteBitmapByCurrentVersion
                    .invoke(SpriteType.Spell)
                    .collectLatest { currentSpriteMap ->
                        _uiState.update { it.copy(currentSpriteMap = currentSpriteMap) }
                    }
            }

            launch {
                getCurrentVersion
                    .invoke()
                    .map { it.name }
                    .distinctUntilChanged()
                    .collectLatest {
                        _refreshJob?.cancel()
                        _uiState.update { it.copy(selectedSpell = null) }
                    }
            }

            launch {
                combine(
                    getCurrentVersionDistinctBySpriteType.invoke(SpriteType.Spell),
                    getSpellListByCurrentVersion.invoke(),
                ) { version, spellList ->
                    if (version.isDownLoadSpellIconSprite || spellList.isEmpty())
                        return@combine null

                    spellList.map { item -> item.image.sprite }.distinct()
                }
                    .filterNotNull()
                    .collectLatest { spriteFileList ->
                        refreshDownloadSpriteBitmap
                            .invoke(SpriteType.Spell, spriteFileList)
                            .onFailure {
                                _sideEffect.emit(SpellHomeSideEffect.ShowErrorMessage(it as Exception))
                            }
                    }
            }
        }
    }
}

data class SpellHomeUiState(
    val isLoading: Boolean = false,
    val currentSpriteMap: Map<String, Bitmap> = mapOf(),
    val displaySpellList: List<List<SummonerSpell>> = listOf(),
    val selectedSpell: SummonerSpell? = null
)

sealed interface SpellHomeAction {
    data object RefreshSpellData : SpellHomeAction

    data class SelectSpell(val spell: SummonerSpell) : SpellHomeAction
    data class ChangeSpan(val span: Int) : SpellHomeAction
}

sealed interface SpellHomeSideEffect {
    data class ShowErrorMessage(val exception: Exception) : SpellHomeSideEffect
}