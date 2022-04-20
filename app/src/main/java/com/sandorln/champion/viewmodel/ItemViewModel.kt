package com.sandorln.champion.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.use_case.GetItemList
import com.sandorln.champion.use_case.GetVersionCategory
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
    getVersionCategory: GetVersionCategory,
    getItemList: GetItemList
) : AndroidViewModel(context as Application) {
    private val _searchItemName: MutableStateFlow<String> = MutableStateFlow("")
    fun changeSearchItemName(searchName: String) = viewModelScope.launch(Dispatchers.IO) { _searchItemName.emit(searchName) }

    private val _inStoreItem: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val inStoreItem: StateFlow<Boolean> get() = _inStoreItem
    fun changeInStoreItem(inStore: Boolean) = viewModelScope.launch(Dispatchers.IO) { _inStoreItem.emit(inStore) }

    val itemVersion = getVersionCategory().mapLatest { it.item }

    val itemList = _searchItemName
        .debounce(250)
        .combine(_inStoreItem) { search, inStore -> Pair(search, inStore) }
        .flatMapLatest { getItemList(it.first, it.second) }
        .onStart { delay(250) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ResultData.Loading)
}