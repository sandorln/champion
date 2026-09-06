package com.sandorln.champion.ui.patch

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.domain.usecase.champion.GetChampionPatchNoteList
import com.sandorln.domain.usecase.item.GetItemPatchNoteList
import com.sandorln.domain.usecase.spell.GetSpellPatchNoteList
import com.sandorln.model.data.patchnote.PatchNoteData
import com.sandorln.model.keys.BundleKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class PatchNoteTab(val title: String) {
    Champion("챔피언"),
    Item("아이템"),
    Spell("소환사 주문")
}

@HiltViewModel
class ChampionPatchNoteListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getChampionPatchNoteList: GetChampionPatchNoteList,
    private val getItemPatchNoteList: GetItemPatchNoteList,
    private val getSpellPatchNoteList: GetSpellPatchNoteList
) : ViewModel() {
    private val _version = savedStateHandle.get<String>(BundleKeys.VERSION) ?: ""

    private val _uiState = MutableStateFlow(ChampionPatchNoteListUiState())
    val uiState = _uiState.asStateFlow()

    private val _action = MutableSharedFlow<ChampionPatchNoteListAction>()
    fun sendAction(action: ChampionPatchNoteListAction) = viewModelScope.launch {
        _action.emit(action)
    }

    private var refreshPatchJob: Job? = null

    private fun loadPatchNotes(tab: PatchNoteTab, force: Boolean = false) {
        if (refreshPatchJob?.isActive == true) return

        refreshPatchJob = viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }

            when (tab) {
                PatchNoteTab.Champion -> {
                    if (force || _uiState.value.championPatchNoteList == null) {
                        val list = getChampionPatchNoteList.invoke(_version).getOrNull() ?: emptyList()
                        _uiState.update { it.copy(championPatchNoteList = list) }
                    }
                }
                PatchNoteTab.Item -> {
                    if (force || _uiState.value.itemPatchNoteList == null) {
                        val list = getItemPatchNoteList.invoke(_version).getOrNull() ?: emptyList()
                        _uiState.update { it.copy(itemPatchNoteList = list) }
                    }
                }
                PatchNoteTab.Spell -> {
                    if (force || _uiState.value.spellPatchNoteList == null) {
                        val list = getSpellPatchNoteList.invoke(_version).getOrNull() ?: emptyList()
                        _uiState.update { it.copy(spellPatchNoteList = list) }
                    }
                }
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    init {
        loadPatchNotes(PatchNoteTab.Champion)

        viewModelScope.launch {
            _action.collect { action ->
                when (action) {
                    is ChampionPatchNoteListAction.ChangeTab -> {
                        _uiState.update { it.copy(selectedTab = action.tab) }
                        loadPatchNotes(action.tab)
                    }
                    ChampionPatchNoteListAction.RefreshChampionPatchNoteList -> {
                        loadPatchNotes(_uiState.value.selectedTab, force = true)
                    }
                }
            }
        }
    }
}

data class ChampionPatchNoteListUiState(
    val isLoading: Boolean = false,
    val selectedTab: PatchNoteTab = PatchNoteTab.Champion,
    val championPatchNoteList: List<PatchNoteData>? = null,
    val itemPatchNoteList: List<PatchNoteData>? = null,
    val spellPatchNoteList: List<PatchNoteData>? = null
) {
    val currentPatchNoteList: List<PatchNoteData>?
        get() = when (selectedTab) {
            PatchNoteTab.Champion -> championPatchNoteList
            PatchNoteTab.Item -> itemPatchNoteList
            PatchNoteTab.Spell -> spellPatchNoteList
        }
}

sealed interface ChampionPatchNoteListAction {
    data object RefreshChampionPatchNoteList : ChampionPatchNoteListAction
    data class ChangeTab(val tab: PatchNoteTab) : ChampionPatchNoteListAction
}
