package com.sandorln.champion.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sandorln.champion.application.ChampionApplication
import com.sandorln.champion.model.ChampionData
import com.sandorln.champion.model.keys.BundleKeys
import com.sandorln.champion.model.type.AppSettingType
import com.sandorln.champion.usecase.GetAppSettingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ChampionDetailViewModel @Inject constructor(
    @ApplicationContext context: Context,
    savedStateHandle: SavedStateHandle,
    private val getAppSettingUseCase: GetAppSettingUseCase
) : AndroidViewModel(context as Application) {
    val championData: LiveData<ChampionData> = savedStateHandle.getLiveData(BundleKeys.CHAMPION_DATA, ChampionData())

    val selectChampionSkinDrawable: MutableStateFlow<Drawable?> = MutableStateFlow(null)
    val selectChampionSkinName: MutableStateFlow<String> = MutableStateFlow(championData.value?.name ?: "")
    val changeSelectSkin: (drawable: Drawable?, skinName: String?) -> Unit = { drawable, skinName ->
        selectChampionSkinDrawable.tryEmit(drawable)
        selectChampionSkinName.tryEmit(skinName ?: championData.value?.name ?: "")
    }

    val isVideoAutoPlay = getApplication<ChampionApplication>()
        .isWifiConnectFlow
        .transform { isWifiConnect ->
            val isVideoWifiModeAutoPlay = getAppSettingUseCase(AppSettingType.VIDEO_WIFI_MODE_AUTO_PLAY)
            emit(if (isVideoWifiModeAutoPlay) isWifiConnect else true)
        }
        .flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)
}