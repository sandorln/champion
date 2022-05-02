package com.sandorln.champion.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.sandorln.champion.application.ChampionApplication
import com.sandorln.champion.model.ChampionData
import com.sandorln.champion.model.keys.BundleKeys
import com.sandorln.champion.model.type.AppSettingType
import com.sandorln.champion.usecase.GetAppSettingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ChampionDetailViewModel @Inject constructor(
    @ApplicationContext context: Context,
    savedStateHandle: SavedStateHandle,
    private val getAppSettingUseCase: GetAppSettingUseCase
) : AndroidViewModel(context as Application) {
    val championData: LiveData<ChampionData> = savedStateHandle.getLiveData(BundleKeys.CHAMPION_DATA, ChampionData())
    suspend fun isVideoWifiModeAutoPlay() = getAppSettingUseCase(AppSettingType.VIDEO_WIFI_MODE_AUTO_PLAY)

    fun getIsWifiConnectFlow(): Flow<Boolean> = (getApplication() as? ChampionApplication)?.isWifiConnectFlow ?: throw Exception("Not Found Wifi Connect")
}