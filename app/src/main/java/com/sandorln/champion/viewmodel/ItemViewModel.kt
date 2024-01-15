package com.sandorln.champion.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.sandorln.model.ItemData
import com.sandorln.model.keys.BundleKeys
import com.sandorln.model.result.ResultData
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

    private val _itemList: MutableStateFlow<com.sandorln.model.result.ResultData<List<com.sandorln.model.ItemData>>> = MutableStateFlow(com.sandorln.model.result.ResultData.Loading)
    val itemList = _itemList
        .onStart {
            when (val result = _itemList.firstOrNull()) {
                is com.sandorln.model.result.ResultData.Success -> {
                    result.data?.let { itemList ->
                        /* 현재 보여지고 있는 아이템 버전과 설정에서 설정된 버전이 다를 시 갱신 */
                        val nowShowItemVersion = itemList.firstOrNull()?.version ?: ""
                        val localItemVersion = getItemVersionUseCase().first()
                        if (nowShowItemVersion != localItemVersion)
                            refreshItemList()
                    }
                }
                /* 오류 상태였을 경우 곧바로 갱신 */
                is com.sandorln.model.result.ResultData.Failed -> refreshItemList()
                else -> {}
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), com.sandorln.model.result.ResultData.Loading)

    fun refreshItemList() = viewModelScope.launch { _itemList.emitAll(getItemListUseCase(_searchItemName.value, true)) }

    private val _itemId = savedStateHandle
        .getLiveData<String>(com.sandorln.model.keys.BundleKeys.ITEM_ID)
        .asFlow()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    fun changeFindItemId(itemId: String) = savedStateHandle.set(com.sandorln.model.keys.BundleKeys.ITEM_ID, itemId)

    val findItemData = _itemId
        .flatMapLatest { itemId -> findItemByIdUseCase(itemId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), com.sandorln.model.result.ResultData.Loading)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _searchItemName
                .transform { search -> emitAll(getItemListUseCase(search, true)) }
                .collectLatest { _itemList.emit(it) }
        }
    }
}