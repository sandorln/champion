package com.sandorln.champion.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import com.sandorln.model.ChampionData
import com.sandorln.model.keys.BundleKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

@HiltViewModel
class ChampionStatusViewModel @Inject constructor(
    @ApplicationContext context: Context,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(context as Application) {
    private val _originalChampionStatus: Flow<com.sandorln.model.ChampionData.ChampionStats> =
        savedStateHandle.getLiveData<com.sandorln.model.ChampionData.ChampionStats>(com.sandorln.model.keys.BundleKeys.CHAMPION_ORIGIN_STATUS_KEY).asFlow()
    private val _otherChampionStatus: Flow<com.sandorln.model.ChampionData.ChampionStats> =
        savedStateHandle.getLiveData<com.sandorln.model.ChampionData.ChampionStats>(com.sandorln.model.keys.BundleKeys.CHAMPION_OTHER_STATUS_KEY).asFlow()

    val originalChampionStatus = _originalChampionStatus.transform { status -> emit(status.changeJsonArray()) }
    val otherChampionStatus = _otherChampionStatus.transform { status -> emit(status.changeJsonArray()) }
}