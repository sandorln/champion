package com.sandorln.champion.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sandorln.champion.model.ChampionData
import com.sandorln.champion.model.keys.BundleKeys
import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.usecase.GetChampionInfoUseCase
import com.sandorln.champion.usecase.GetChampionVersionByVersionUseCase
import com.sandorln.champion.usecase.GetVersionListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
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
    private val versionList: StateFlow<List<String>> = getVersionListUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), mutableListOf())

    private val _championId: String = savedStateHandle.get<String>(BundleKeys.CHAMPION_ID) ?: ""
    private val _firstChampionVersion: MutableStateFlow<String> = MutableStateFlow("")
    private val _secondChampionVersion: MutableStateFlow<String> = MutableStateFlow("")

    private val _errorMsg: MutableSharedFlow<Exception> = MutableSharedFlow()
    val errorMsg = _errorMsg.shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    private val championInfoFlowCollector: suspend FlowCollector<ChampionData.ChampionStats>.(value: String) -> Unit = { version ->
        val championVersion = getChampionVersionByVersionUseCase(version).first()
        if (championVersion.isNotEmpty()) {
            try {
                val championData = (getChampionInfoUseCase(championVersion, _championId).last() as? ResultData.Success<ChampionData>)?.data ?: throw Exception("데이터를 불러올 수 없습니다")
                emit(championData.stats)
            } catch (e: Exception) {
                _errorMsg.emit(e)
            }
        }
    }

    val firstChampionStatus: Flow<ChampionData.ChampionStats> = _firstChampionVersion.transform(championInfoFlowCollector)
    val secondChampionStatus: Flow<ChampionData.ChampionStats> = _secondChampionVersion.transform(championInfoFlowCollector)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val championInitVersion = savedStateHandle.get<String>(BundleKeys.CHAMPION_VERSION) ?: ""
            _firstChampionVersion.emit(championInitVersion)
            _secondChampionVersion.emit("4.3.12")
        }
    }
}