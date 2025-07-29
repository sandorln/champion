package com.sandorln.rune.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.domain.usecase.rune.GetRunePatchNoteList
import com.sandorln.domain.usecase.rune.GetRuneStyleListByCurrentVersion
import com.sandorln.domain.usecase.version.GetCurrentVersion
import com.sandorln.model.data.patchnote.PatchNoteData
import com.sandorln.model.data.rune.RuneData
import com.sandorln.model.data.rune.RuneStyle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RuneHomeViewModel @Inject constructor(
    private val getCurrentVersion: GetCurrentVersion,
    getRuneStyleListByCurrentVersion: GetRuneStyleListByCurrentVersion,
    private val getRunePatchNoteList: GetRunePatchNoteList
) : ViewModel() {
    private val _uiState = MutableStateFlow(RuneHomeUiState())
    val uiState = _uiState.asStateFlow()

    fun sendAction(runeHomeAction: RuneHomeAction) {
        when (runeHomeAction) {
            RuneHomeAction.RefreshRuneData -> refreshRuneData()
            is RuneHomeAction.SelectedRuneStyle -> {
                _uiState.update {
                    it.copy(
                        selectedRuneStyle = runeHomeAction.runeStyle,
                        selectedRuneDataList = List(runeHomeAction.runeStyle.slots.size) { null })
                }
            }

            is RuneHomeAction.SelectedRuneDataId -> {
                val runeDataList = _uiState.value.selectedRuneDataList.toMutableList()
                val previousRuneData = runeDataList[runeHomeAction.runeSlotIndex]
                val tempRuneData = runeHomeAction.runeData.takeIf { previousRuneData?.id != it.id }
                runeDataList[runeHomeAction.runeSlotIndex] = tempRuneData
                _uiState.update { it.copy(selectedRuneDataList = runeDataList) }
            }
        }
    }

    private var _refreshRuneDataJob: Job? = null
    private fun refreshRuneData() {
        _refreshRuneDataJob?.cancel()
        _refreshRuneDataJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, runePatchNoteList = null) }
            val currentVersion = getCurrentVersion.invoke().first().name
            val runePatchNoteList = getRunePatchNoteList
                .invoke(currentVersion)
                .getOrDefault(emptyList())
            _uiState.update { it.copy(isLoading = false, runePatchNoteList = runePatchNoteList) }
        }
    }

    init {
        viewModelScope.launch {
            launch {
                getRuneStyleListByCurrentVersion
                    .invoke()
                    .collect { runeStyleList ->
                        _uiState.update {
                            it.copy(
                                runeStyleList = runeStyleList,
                                selectedRuneStyle = runeStyleList.firstOrNull(),
                                selectedRuneDataList = List(runeStyleList.firstOrNull()?.slots?.size ?: 0) { null },
                                notRuneSystem = runeStyleList.isEmpty()
                            )
                        }
                    }
            }
            launch {
                getCurrentVersion
                    .invoke()
                    .map { it.name }
                    .distinctUntilChanged()
                    .collect { versionName ->
                        _uiState.update { it.copy(runePatchNoteList = null) }
                        val runePatchNoteList = getRunePatchNoteList.invoke(versionName).getOrDefault(emptyList())
                        _uiState.update { it.copy(runePatchNoteList = runePatchNoteList) }
                    }
            }
        }
    }
}

data class RuneHomeUiState(
    val isLoading: Boolean = false,
    val selectedRuneStyle: RuneStyle? = null,
    val selectedRuneDataList: List<RuneData?> = listOf(),
    val runeStyleList: List<RuneStyle> = emptyList(),
    val notRuneSystem: Boolean = false,
    val runePatchNoteList: List<PatchNoteData>? = null
)

sealed interface RuneHomeAction {
    data object RefreshRuneData : RuneHomeAction

    data class SelectedRuneDataId(
        val runeSlotIndex: Int,
        val runeData: RuneData
    ) : RuneHomeAction

    data class SelectedRuneStyle(val runeStyle: RuneStyle) : RuneHomeAction
}