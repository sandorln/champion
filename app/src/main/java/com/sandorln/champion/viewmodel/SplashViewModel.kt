package com.sandorln.champion.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.model.result.ResultData
import com.sandorln.champion.usecase.ChangeNewestVersionUseCase
import com.sandorln.champion.usecase.HasNewLolVersionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val hasNewLolVersionUseCase: HasNewLolVersionUseCase,
    private val changeNewestVersionUseCase: ChangeNewestVersionUseCase
) : AndroidViewModel(context as Application) {
    init {
        refreshHasNewLolVersion()
    }

    private val _hasNewLolVersion: MutableStateFlow<ResultData<Boolean>> = MutableStateFlow(com.sandorln.model.result.ResultData.Loading)
    val hasNewLolVersion = _hasNewLolVersion.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), com.sandorln.model.result.ResultData.Loading)
    fun refreshHasNewLolVersion() {
        viewModelScope.launch { _hasNewLolVersion.emitAll(hasNewLolVersionUseCase()) }
    }

    suspend fun changeNewestVersion() = changeNewestVersionUseCase()
}