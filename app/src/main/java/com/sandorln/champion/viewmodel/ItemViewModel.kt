package com.sandorln.champion.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.use_case.GetItemList
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemViewModel @Inject constructor(
    @ApplicationContext context: Context,
    getItemList: GetItemList
) : AndroidViewModel(context as Application) {
    private val _searchItemName: MutableStateFlow<String> = MutableStateFlow("")
    val isEmptySearchItemName = _searchItemName.map { it.isEmpty() }
    fun changeSearchItemName(searchName: String) = viewModelScope.launch(Dispatchers.IO) { _searchItemName.emit(searchName) }

    val itemList = _searchItemName
        .debounce(250)
        .flatMapLatest { search -> getItemList(search) }
        .onStart { delay(250) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ResultData.Loading)
}