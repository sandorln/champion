package com.sandorln.champion.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sandorln.champion.usecase.GetChampionInfoUseCase
import com.sandorln.champion.usecase.GetChampionVersionByVersionUseCase
import com.sandorln.champion.usecase.GetVersionListUseCase
import com.sandorln.model.data.champion.ChampionData
import com.sandorln.model.data.champion.ChampionStats
import com.sandorln.model.keys.BundleKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChampionStatusCompareViewModel @Inject constructor(
    @ApplicationContext context: Context,
    savedStateHandle: SavedStateHandle,
    getVersionListUseCase: GetVersionListUseCase,
    private val getChampionInfoUseCase: GetChampionInfoUseCase,
    private val getChampionVersionByVersionUseCase: GetChampionVersionByVersionUseCase
) : AndroidViewModel(context as Application) {
    val versionList = getVersionListUseCase()

    private val _championId: String = savedStateHandle.get<String>(BundleKeys.CHAMPION_ID) ?: ""

    private val initVersion = savedStateHandle.get<String>(BundleKeys.CHAMPION_VERSION) ?: ""
    private val _firstChampionVersion: MutableStateFlow<String> = MutableStateFlow(initVersion)
    val firstChampionVersion = _firstChampionVersion.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), initVersion)
    fun changeFirstVersion(selectVersion: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _firstChampionVersion.emit(selectVersion)
        }
    }

    private val _secondChampionVersion: MutableStateFlow<String> = MutableStateFlow(initVersion)
    val secondChampionVersion = _secondChampionVersion.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), initVersion)
    fun changeSecondVersion(selectVersion: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _secondChampionVersion.emit(selectVersion)
        }
    }

    private val _errorMsg: MutableSharedFlow<Exception> = MutableSharedFlow()
    val errorMsg = _errorMsg.shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    private val championInfoFlowCollector: suspend FlowCollector<ChampionStats>.(value: String) -> Unit = { version ->
        val championVersion = getChampionVersionByVersionUseCase(version).first()
        if (championVersion.isNotEmpty()) {
            try {
                val championData = (getChampionInfoUseCase(championVersion, _championId).last() as? com.sandorln.model.result.ResultData.Success<ChampionData>)?.data
                    ?: throw Exception("데이터를 불러올 수 없습니다")
                emit(championData.stats)
            } catch (e: Exception) {
                emit(ChampionStats())
                _errorMsg.emit(e)
            }
        }
    }

    val firstChampionStatus: Flow<ChampionStats> = _firstChampionVersion.transform(championInfoFlowCollector)
    val secondChampionStatus: Flow<ChampionStats> = _secondChampionVersion.transform(championInfoFlowCollector)
}