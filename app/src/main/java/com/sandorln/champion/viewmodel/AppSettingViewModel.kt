package com.sandorln.champion.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.champion.usecase.ChangeVersionUseCase
import com.sandorln.champion.usecase.GetVersionUseCase
import com.sandorln.champion.usecase.GetVersionListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppSettingViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val getVersionUseCase: GetVersionUseCase,
    getVersionListUseCase: GetVersionListUseCase,
    private val changeVersionUseCase: ChangeVersionUseCase
) : AndroidViewModel(context as Application) {
    init {
        refreshVersion()
    }

    private val _version: MutableStateFlow<String> = MutableStateFlow("")
    val version: StateFlow<String> = _version.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    val versionList = getVersionListUseCase()


    fun changeLolVersion(version: String) = viewModelScope.launch {
        changeVersionUseCase(version)
        refreshVersion()
    }

    private fun refreshVersion() {
        getVersionUseCase()
            .onEach { version -> _version.emit(version) }
            .launchIn(viewModelScope)
    }

}