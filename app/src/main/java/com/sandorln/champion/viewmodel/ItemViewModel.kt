package com.sandorln.champion.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.sandorln.champion.model.ItemData
import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.usecase.FindItemByIdUseCase
import com.sandorln.champion.usecase.GetItemListUseCase
import com.sandorln.champion.usecase.GetItemVersionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val savedStateHandle: SavedStateHandle,
    private val getItemVersionUseCase: GetItemVersionUseCase,
    private val getItemListUseCase: GetItemListUseCase,
    findItemByIdUseCase: FindItemByIdUseCase
) : AndroidViewModel(context as Application) {
    private val _searchItemName: MutableStateFlow<String> = MutableStateFlow("")
    fun changeSearchItemName(searchName: String) = viewModelScope.launch(Dispatchers.IO) { _searchItemName.emit(searchName) }

    /* 이후 판매 중이 아닌 아이템도 볼 수 있도록 설정 */
    private val _inStoreItem: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val inStoreItem: StateFlow<Boolean> = _inStoreItem.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), true)

    private val _itemList: MutableStateFlow<ResultData<List<ItemData>>> = MutableStateFlow(ResultData.Loading)
    val itemList = _itemList
        .onStart {
            when (val result = _itemList.firstOrNull()) {
                is ResultData.Success -> {
                    result.data?.let { itemList ->
                        /* 현재 보여지고 있는 아이템 버전과 설정에서 설정된 버전이 다를 시 갱신 */
                        val nowShowItemVersion = itemList.firstOrNull()?.version ?: ""
                        val localItemVersion = getItemVersionUseCase().first()
                        if (nowShowItemVersion != localItemVersion)
                            refreshItemList()
                    }
                }
                /* 오류 상태였을 경우 곧바로 갱신 */
                is ResultData.Failed -> refreshItemList()
                else -> {}
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ResultData.Loading)

    fun refreshItemList() = viewModelScope.launch { _itemList.emitAll(getItemListUseCase(_searchItemName.value, true)) }

    private val _itemId = savedStateHandle
        .getLiveData<String>("itemId")
        .asFlow()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    fun changeFindItemId(itemId: String) = savedStateHandle.set("itemId", itemId)

    val findItemData = _itemId
        .flatMapLatest { itemId -> findItemByIdUseCase(itemId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ResultData.Loading)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _searchItemName
                .transform { search -> emitAll(getItemListUseCase(search, true)) }
                .collectLatest { _itemList.emit(it) }
        }
    }
}