package com.sandorln.champion.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import com.google.gson.JsonArray
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
    private val _originalChampionStatus: Flow<ChampionData.ChampionStats> =
        savedStateHandle.getLiveData<ChampionData.ChampionStats>(BundleKeys.CHAMPION_ORIGIN_STATUS_KEY).asFlow()
    private val _otherChampionStatus: Flow<ChampionData.ChampionStats> =
        savedStateHandle.getLiveData<ChampionData.ChampionStats>(BundleKeys.CHAMPION_OTHER_STATUS_KEY).asFlow()

    val originalChampionStatus = _originalChampionStatus.transform { _ -> emit(JsonArray()) }
    val otherChampionStatus = _otherChampionStatus.transform { _ -> emit(JsonArray()) }
}