package com.sandorln.champion.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.champion.usecase.ChangeVersion
import com.sandorln.champion.usecase.GetVersion
import com.sandorln.champion.usecase.GetVersionList
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppSettingViewModel @Inject constructor(
    @ApplicationContext context: Context,
    getVersion: GetVersion,
    getVersionList: GetVersionList,
    private val changeVersion: ChangeVersion
) : AndroidViewModel(context as Application) {
    private val versionRefresh = MutableStateFlow(true)
    val version : StateFlow<String> = versionRefresh
        .distinctUntilChangedBy { it }
        .flatMapLatest {
            versionRefresh.emit(false)
            getVersion()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")
    val versionList = getVersionList()

    fun changeLolVersion(version: String) = viewModelScope.launch {
        changeVersion(version)
        versionRefresh.emit(true)
    }
}