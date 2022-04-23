package com.sandorln.champion.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sandorln.champion.model.ChampionData
import com.sandorln.champion.model.keys.BundleKeys
import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.usecase.GetChampionInfo
import com.sandorln.champion.usecase.GetChampionList
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChampionViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val savedStateHandle: SavedStateHandle,
    private val getChampionList: GetChampionList,
    private val getChampionInfo: GetChampionInfo
) : AndroidViewModel(context as Application) {
    private val _searchChampionName: MutableStateFlow<String> = MutableStateFlow("")
    val searchChampionData: StateFlow<String> get() = _searchChampionName
    fun changeSearchChampionName(searchName: String) = viewModelScope.launch(Dispatchers.IO) { _searchChampionName.emit(searchName) }

    val showChampionList = _searchChampionName
        .debounce(250)
        .flatMapLatest { search -> getChampionList(search) }
        .onStart { delay(250) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ResultData.Loading)

    suspend fun getChampionDetailInfo(championId: String) = getChampionInfo(championId).firstOrNull()
    val championData: LiveData<ChampionData> = savedStateHandle.getLiveData(BundleKeys.CHAMPION_DATA)
}