package com.sandorln.rune.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.domain.usecase.rune.GetRuneStyleListByCurrentVersion
import com.sandorln.model.data.rune.RuneStyle
import dagger.hilt.android.lifecycle.HiltViewModel
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

    init {
        viewModelScope.launch {
            launch {
                getRuneStyleListByCurrentVersion
                    .invoke()
                    .collect { runeStyleList ->
                        _uiState.update { it.copy(runeStyleList = runeStyleList) }
                    }
            }
        }
    }
}

data class RuneHomeUiState(
    val isLoading: Boolean = false,
    val runeStyleList: List<RuneStyle> = emptyList(),
)