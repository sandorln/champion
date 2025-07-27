package com.sandorln.rune.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.domain.usecase.rune.GetRuneStyleListByCurrentVersion
import com.sandorln.model.data.rune.RuneStyle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RuneHomeViewModel @Inject constructor(
    getRuneStyleListByCurrentVersion: GetRuneStyleListByCurrentVersion
) : ViewModel() {
    private val _uiState = MutableStateFlow(RuneHomeUiState())
    val uiState = _uiState.asStateFlow()

    fun sendAction(runeHomeAction: RuneHomeAction) {
        when (runeHomeAction) {
            RuneHomeAction.RefreshRuneData -> refreshRuneData()
            is RuneHomeAction.SelectedRuneStyle -> {
                _uiState.update { it.copy(selectedRuneStyle = runeHomeAction.runeStyle) }
            }
        }
    }

    private fun refreshRuneData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // TODO :: 갱신 로직 추가
            delay(1000)
            _uiState.update { it.copy(isLoading = false) }
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
                                runeStyleList = runeStyleList.sortedBy(RuneStyle::id),
                                selectedRuneStyle = it.selectedRuneStyle ?: runeStyleList.firstOrNull()
                            )
                        }
                    }
            }
        }
    }
}

data class RuneHomeUiState(
    val isLoading: Boolean = false,
    val selectedRuneStyle: RuneStyle? = null,
    val runeStyleList: List<RuneStyle> = emptyList(),
)

sealed interface RuneHomeAction {
    data object RefreshRuneData : RuneHomeAction

    data class SelectedRuneStyle(val runeStyle: RuneStyle) : RuneHomeAction
}