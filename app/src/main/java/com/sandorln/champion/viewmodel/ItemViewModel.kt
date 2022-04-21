package com.sandorln.champion.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.use_case.FindItemById
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
    private val savedStateHandle: SavedStateHandle,
    getItemList: GetItemList,
    findItemById: FindItemById
) : AndroidViewModel(context as Application) {
    private val _searchItemName: MutableStateFlow<String> = MutableStateFlow("")
    fun changeSearchItemName(searchName: String) = viewModelScope.launch(Dispatchers.IO) { _searchItemName.emit(searchName) }

    private val _inStoreItem: MutableStateFlow<Boolean> = MutableStateFlow(true)

    val itemList = _searchItemName
        .debounce(250)
        .combine(_inStoreItem) { search, inStore -> Pair(search, inStore) }
        .flatMapLatest { getItemList(it.first, it.second) }
        .onStart { delay(250) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, ResultData.Loading)


    private val _itemId = savedStateHandle
        .getLiveData<String>("itemId")
        .asFlow()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    fun changeFindItemId(itemId: String) = savedStateHandle.set("itemId", itemId)

    val findItemData = _itemId
        .flatMapLatest { itemId -> findItemById(itemId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ResultData.Loading)
}