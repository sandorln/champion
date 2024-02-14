package com.sandorln.item.ui.home

import androidx.compose.ui.util.fastFilter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.domain.usecase.item.GetItemListByCurrentVersion
import com.sandorln.domain.usecase.item.GetNewItemIdListByCurrentVersion
import com.sandorln.domain.usecase.sprite.GetCurrentVersionDistinctBySpriteType
import com.sandorln.domain.usecase.sprite.GetSpriteBitmapByCurrentVersion
import com.sandorln.domain.usecase.sprite.RefreshDownloadSpriteBitmap
import com.sandorln.model.data.image.SpriteType
import com.sandorln.model.data.item.ItemData
import com.sandorln.model.data.map.MapType
import com.sandorln.model.type.ItemTagType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class ItemHomeViewModel @Inject constructor(
    getItemListByCurrentVersion: GetItemListByCurrentVersion,
    getNewItemIdListByCurrentVersion: GetNewItemIdListByCurrentVersion,
    refreshDownloadSpriteBitmap: RefreshDownloadSpriteBitmap,
    getSpriteBitmapByCurrentVersion: GetSpriteBitmapByCurrentVersion,
    getCurrentVersionDistinctBySpriteType: GetCurrentVersionDistinctBySpriteType,
) : ViewModel() {
    private val _itemUiState = MutableStateFlow(ItemHomeUiState())
    val itemUiState = _itemUiState.asStateFlow()

    private val _itemAction = MutableSharedFlow<ItemHomeAction>()
    fun sendAction(itemHomeAction: ItemHomeAction) = viewModelScope.launch {
        _itemAction.emit(itemHomeAction)
    }

    private val _itemFilter: suspend (ItemHomeUiState, List<ItemData>, List<String>) -> List<ItemData> = { uiState, itemList, newItemIdList ->
        val searchKeyword = uiState.searchKeyword
        val selectMapType = uiState.isSelectMapType
        val filterItemList = if (uiState.isSelectNewItem) {
            itemList.fastFilter { newItemIdList.contains(it.id) }
        } else {
            itemList
        }

        filterItemList.filter { item ->
            if (!item.inStore) return@filter false

            /* Tag Type Filter */
            when {
                uiState.selectTag.isEmpty() -> {}
                !item.tags.containsAll(uiState.selectTag) -> return@filter false
            }

            /* Map Type Filter */
            when {
                selectMapType == MapType.ALL && item.mapType != MapType.NONE -> {}
                item.mapType == MapType.ALL && (selectMapType == MapType.SUMMONER_RIFT || selectMapType == MapType.ARAM) -> {}
                selectMapType != item.mapType -> return@filter false
            }
            item.name.contains(searchKeyword)
        }
    }

    private val _itemMutex = Mutex()

    private val _currentItemList = getItemListByCurrentVersion.invoke().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _currentNewItemIdList = getNewItemIdListByCurrentVersion.invoke().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val currentSpriteMap = getSpriteBitmapByCurrentVersion.invoke(SpriteType.Item).stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyMap())

    val displayItemList = combine(_itemUiState, _currentItemList, _currentNewItemIdList, _itemFilter)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    init {
        viewModelScope.launch {
            launch {
                _itemAction.collect { action ->
                    _itemMutex.withLock {
                        val currentUiState = _itemUiState.value
                        when (action) {
                            is ItemHomeAction.RefreshItemData -> {
                                _itemUiState.emit(currentUiState.copy(isLoading = true))
                                val spriteFileList = _currentItemList.value.map { item -> item.image.sprite }.distinct()

                                refreshDownloadSpriteBitmap.invoke(
                                    spriteType = SpriteType.Item,
                                    fileNameList = spriteFileList
                                ).onFailure {
                                    /* TODO :: 에러 처리 필요 */
                                }

                                _itemUiState.emit(currentUiState.copy(isLoading = false))
                            }

                            is ItemHomeAction.ChangeItemSearchKeyword -> {
                                _itemUiState.emit(currentUiState.copy(searchKeyword = action.searchKeyword))
                            }

                            is ItemHomeAction.ChangeMapTypeFilter -> {
                                _itemUiState.emit(currentUiState.copy(isSelectMapType = action.mapType))
                            }

                            is ItemHomeAction.ToggleItemTagType -> {
                                val isSelected = currentUiState.selectTag.contains(action.itemTagType)
                                val selectTag = currentUiState.selectTag.toMutableSet()
                                if (isSelected) {
                                    selectTag.remove(action.itemTagType)
                                } else {
                                    selectTag.add(action.itemTagType)
                                }
                                _itemUiState.emit(currentUiState.copy(selectTag = selectTag.toSet()))
                            }

                            is ItemHomeAction.ToggleSelectNewItem -> {
                                _itemUiState.emit(currentUiState.copy(isSelectNewItem = !currentUiState.isSelectNewItem))
                            }

                            is ItemHomeAction.SelectItemData -> {
                                _itemUiState.emit(currentUiState.copy(selectedItemData = action.itemData))
                            }
                        }
                    }
                }
            }

            launch(Dispatchers.IO) {
                combine(getCurrentVersionDistinctBySpriteType.invoke(SpriteType.Item), _currentItemList) { version, itemList ->
                    if (version.isDownLoadItemIconSprite || itemList.isEmpty())
                        return@combine null

                    itemList.map { item -> item.image.sprite }.distinct()
                }.filterNotNull()
                    .collectLatest { spriteFileList ->
                        refreshDownloadSpriteBitmap.invoke(
                            spriteType = SpriteType.Item,
                            fileNameList = spriteFileList
                        ).onFailure {
                            /* TODO :: 오류 발생 시 처리 */
                        }
                    }
            }
        }
    }
}

data class ItemHomeUiState(
    val isLoading: Boolean = false,
    val searchKeyword: String = "",
    val isSelectMapType: MapType = MapType.ALL,
    val selectTag: Set<ItemTagType> = emptySet(),
    val isSelectNewItem: Boolean = false,
    val selectedItemData: ItemData? = null
)

sealed interface ItemHomeAction {
    data object RefreshItemData : ItemHomeAction
    data object ToggleSelectNewItem : ItemHomeAction

    data class ToggleItemTagType(val itemTagType: ItemTagType) : ItemHomeAction
    data class ChangeMapTypeFilter(val mapType: MapType) : ItemHomeAction
    data class ChangeItemSearchKeyword(val searchKeyword: String) : ItemHomeAction
    data class SelectItemData(val itemData: ItemData?) : ItemHomeAction
}