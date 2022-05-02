package com.sandorln.champion.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.champion.model.type.AppSettingType
import com.sandorln.champion.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppSettingViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val getVersionUseCase: GetVersionUseCase,
    getVersionListUseCase: GetVersionListUseCase,
    private val changeVersionUseCase: ChangeVersionUseCase,
    private val getAppSettingUseCase: GetAppSettingUseCase,
    private val toggleAppSettingUseCase: ToggleAppSettingUseCase
) : AndroidViewModel(context as Application) {
    private val _version: MutableStateFlow<String> = MutableStateFlow("").apply {
        /* 초기 값 설정 */
        viewModelScope.launch { emit(getVersionUseCase().last()) }
    }

    val version: StateFlow<String> = _version.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")
    val versionList = getVersionListUseCase()
    fun changeLolVersion(version: String) = viewModelScope.launch(Dispatchers.IO) {
        changeVersionUseCase(version)
        _version.emit(version)
    }

    private val _questionNewestLolVersion = MutableStateFlow(false).apply {
        viewModelScope.launch {
            /* 초기 값 설정 */
            emit(getAppSettingUseCase(AppSettingType.QUESTION_NEWEST_LOL_VERSION))
        }
    }
    val questionNewestLolVersion: StateFlow<Boolean> = _questionNewestLolVersion.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)
    fun changeAppSetting(appSettingType: AppSettingType) {
        viewModelScope.launch(Dispatchers.IO) {
            val toggleValue = toggleAppSettingUseCase(appSettingType)
            when (appSettingType) {
                AppSettingType.QUESTION_NEWEST_LOL_VERSION -> _questionNewestLolVersion.emit(toggleValue)
            }
        }
    }
}