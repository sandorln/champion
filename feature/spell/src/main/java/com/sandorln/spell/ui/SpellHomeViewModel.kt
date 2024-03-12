package com.sandorln.spell.ui

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpellHomeViewModel @Inject constructor(
    getSpellListByCurrentVersion: GetSpellListByCurrentVersion,
    refreshDownloadSpriteBitmap: RefreshDownloadSpriteBitmap,
    getCurrentVersionDistinctBySpriteType: GetCurrentVersionDistinctBySpriteType,
    getSpriteBitmapByCurrentVersion: GetSpriteBitmapByCurrentVersion,
    getCurrentVersion: GetCurrentVersion
) : ViewModel() {
    private val _uiState = MutableStateFlow(SpellHomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _action = MutableSharedFlow<SpellHomeAction>()
    fun sendAction(action: SpellHomeAction) = viewModelScope.launch {
        _action.emit(action)
    }

    val currentSpriteMap = getSpriteBitmapByCurrentVersion.invoke(SpriteType.Spell).stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyMap())
    val currentSpellList = getSpellListByCurrentVersion.invoke().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    init {
        viewModelScope.launch {
            launch {
                getCurrentVersion
                    .invoke()
                    .distinctUntilChanged()
                    .collectLatest {
                        _uiState.update { it.copy(selectedSpell = null) }
                    }
            }
            launch {
                _action.collect { action ->
                    when (action) {
                        SpellHomeAction.RefreshSpellData -> {
                            _uiState.update { it.copy(isLoading = true) }
                            val spriteFileList = currentSpellList.value.map { spell -> spell.image.sprite }.distinct()
                            refreshDownloadSpriteBitmap
                                .invoke(
                                    SpriteType.Spell,
                                    spriteFileList
                                ).onFailure {
                                    /* TODO :: 오류 발생 시 처리 */
                                }
                            _uiState.update { it.copy(isLoading = false) }
                        }

                        is SpellHomeAction.SelectSpell -> {
                            val tempSpell = action.spell.takeIf {
                                _uiState.value.selectedSpell?.id != it.id
                            }
                            _uiState.update { it.copy(selectedSpell = tempSpell) }
                        }
                    }
                }
            }

            launch(Dispatchers.IO) {
                combine(getCurrentVersionDistinctBySpriteType.invoke(SpriteType.Spell), currentSpellList) { version, spellList ->
                    if (version.isDownLoadSpellIconSprite || spellList.isEmpty())
                        return@combine null

                    spellList.map { item -> item.image.sprite }.distinct()
                }.filterNotNull()
                    .collectLatest { spriteFileList ->
                        refreshDownloadSpriteBitmap
                            .invoke(
                                SpriteType.Spell,
                                spriteFileList
                            ).onFailure {
                                /* TODO :: 오류 발생 시 처리 */
                            }
                    }
            }
        }
    }
}

data class SpellHomeUiState(
    val isLoading: Boolean = false,
    val selectedSpell: SummonerSpell? = null
)

sealed interface SpellHomeAction {
    data object RefreshSpellData : SpellHomeAction
    data class SelectSpell(val spell: SummonerSpell) : SpellHomeAction
}