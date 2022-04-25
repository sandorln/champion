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
    private val getVersion: GetVersion,
    getVersionList: GetVersionList,
    private val changeVersion: ChangeVersion
) : AndroidViewModel(context as Application) {
    init {
        refreshVersion()
    }

    private val _version: MutableStateFlow<String> = MutableStateFlow("")
    val version: StateFlow<String> = _version.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    val versionList = getVersionList()


    fun changeLolVersion(version: String) = viewModelScope.launch {
        changeVersion(version)
        refreshVersion()
    }

    private fun refreshVersion() {
        getVersion()
            .onEach { version -> _version.emit(version) }
            .launchIn(viewModelScope)
    }

}