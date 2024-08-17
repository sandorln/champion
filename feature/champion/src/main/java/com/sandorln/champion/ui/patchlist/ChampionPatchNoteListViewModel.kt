package com.sandorln.champion.ui.patchlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.domain.usecase.champion.GetChampionPatchNoteList
import com.sandorln.model.data.patchnote.PatchNoteData
import com.sandorln.model.keys.BundleKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChampionPatchNoteListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getChampionPatchNoteList: GetChampionPatchNoteList
) : ViewModel() {
    private val _version = savedStateHandle.get<String>(BundleKeys.VERSION) ?: ""

    private val _uiState = MutableStateFlow(ChampionPatchNoteListUiState())
    val uiState = _uiState.asStateFlow()

    private val _action = MutableSharedFlow<ChampionPatchNoteListAction>()
    fun sendAction(action: ChampionPatchNoteListAction) = viewModelScope.launch {
        _action.emit(action)
    }

    private var refreshPatchJob: Job? = null
    private fun refreshChampionPatchNoteList() {
        if (refreshPatchJob?.isActive == true) return

        refreshPatchJob = viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }

            val championPatchNoteList = getChampionPatchNoteList.invoke(_version).getOrNull() ?: emptyList()
            delay(100)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    championPatchNoteList = championPatchNoteList
                )
            }
        }
    }

    init {
        refreshChampionPatchNoteList()

        viewModelScope.launch {
            launch {
                _action.collect{action->
                    when(action){
                        ChampionPatchNoteListAction.RefreshChampionPatchNoteList -> refreshChampionPatchNoteList()
                    }
                }
            }
        }
    }
}

data class ChampionPatchNoteListUiState(
    val isLoading: Boolean = false,
    val championPatchNoteList: List<PatchNoteData>? = null
)

sealed interface ChampionPatchNoteListAction {
    data object RefreshChampionPatchNoteList : ChampionPatchNoteListAction
}
