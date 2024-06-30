package com.sandorln.item.ui.home

import androidx.compose.ui.util.fastFilter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.domain.usecase.item.GetItemListByCurrentVersion
import com.sandorln.domain.usecase.item.GetNewItemIdListByCurrentVersion
import com.sandorln.domain.usecase.sprite.GetCurrentVersionDistinctBySpriteType
import com.sandorln.domain.usecase.sprite.GetSpriteBitmapByCurrentVersion
import com.sandorln.domain.usecase.sprite.RefreshDownloadSpriteBitmap
import com.sandorln.domain.usecase.version.GetCurrentVersion
import com.sandorln.model.data.image.SpriteType
import com.sandorln.model.data.item.ItemData
import com.sandorln.model.data.map.MapType
import com.sandorln.model.type.ItemTagType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
    getCurrentVersion: GetCurrentVersion
) : ViewModel() {
    private val _itemUiState = MutableStateFlow(ItemHomeUiState())
    val itemUiState = _itemUiState.asStateFlow()

    private val _itemAction = MutableSharedFlow<ItemHomeAction>()
    fun sendAction(itemHomeAction: ItemHomeAction) = viewModelScope.launch {
        _itemAction.emit(itemHomeAction)
    }

    private val _sideEffect = MutableSharedFlow<ItemHomeSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    private val _itemFilter: suspend (ItemHomeUiState, List<ItemData>, List<String>) -> List<ItemData> = { uiState, itemList, newItemIdList ->
        val searchKeyword = uiState.searchKeyword
        val selectMapType = uiState.selectMapType
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
            val isMatchMapType = item.mapType == selectMapType
            val isItemAllType = item.mapType == MapType.ALL && (selectMapType == MapType.SUMMONER_RIFT || selectMapType == MapType.ARAM)

            return@filter when {
                isMatchMapType || isItemAllType -> item.name.contains(searchKeyword)
                else -> false
            }
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
                getCurrentVersion
                    .invoke()
                    .map { it.name }
                    .distinctUntilChanged()
                    .collectLatest { version ->
                        _itemMutex.withLock {
                            _itemUiState.update {
                                it.copy(
                                    currentVersionName = version
                                )
                            }
                        }
                    }
            }
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
                                    _sideEffect.emit(ItemHomeSideEffect.ShowErrorMessage(it as Exception))
                                }

                                _itemUiState.emit(currentUiState.copy(isLoading = false))
                            }

                            is ItemHomeAction.ChangeItemSearchKeyword -> {
                                _itemUiState.emit(currentUiState.copy(searchKeyword = action.searchKeyword))
                            }

                            is ItemHomeAction.ChangeMapTypeFilter -> {
                                _itemUiState.emit(currentUiState.copy(selectMapType = action.mapType))
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
                                _itemUiState.emit(currentUiState.copy(selectedItemId = action.itemDataId))
                            }

                            ItemHomeAction.ToggleVisibleFilterBody -> {
                                _itemUiState.update { it.copy(isShowFilterBody = !it.isShowFilterBody) }
                            }

                            ItemHomeAction.ToggleVisibleBuildBody -> {
                                _itemUiState.update { it.copy(isShowBuildBody = !it.isShowBuildBody) }
                            }

                            is ItemHomeAction.AddItemBuild -> {
                                val itemBuildList = _itemUiState.value.itemBuildList
                                val addItemData = action.itemData
                                val shouldAddItemBuildList = itemBuildList.size < 6
                                val hasSameLegendItem = addItemData.depth > 2 && itemBuildList.any { it.id == addItemData.id }

                                if (shouldAddItemBuildList && !hasSameLegendItem) {
                                    _itemUiState.update { uiState ->
                                        val itemBuildSet = uiState
                                            .itemBuildList
                                            .toMutableList()
                                            .apply {
                                                add(addItemData)
                                            }

                                        uiState.copy(itemBuildList = itemBuildSet)
                                    }
                                } else {
                                    _sideEffect.emit(ItemHomeSideEffect.ShowErrorMessage(Exception("해당 아이템을 추가할 수 없습니다")))
                                }
                            }

                            is ItemHomeAction.DeleteItemBuild -> {
                                _itemUiState.update { uiState ->
                                    val itemBuildList = uiState
                                        .itemBuildList
                                        .toMutableList()
                                        .apply { removeAt(action.index) }

                                    uiState.copy(itemBuildList = itemBuildList)
                                }
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
                            _sideEffect.emit(ItemHomeSideEffect.ShowErrorMessage(it as Exception))
                        }
                    }
            }
        }
    }
}

data class ItemHomeUiState(
    val isLoading: Boolean = false,
    val searchKeyword: String = "",
    val selectMapType: MapType = MapType.SUMMONER_RIFT,
    val selectTag: Set<ItemTagType> = emptySet(),
    val isSelectNewItem: Boolean = false,
    val selectedItemId: String? = null,
    val currentVersionName: String = "",
    val isShowFilterBody: Boolean = false,
    val isShowBuildBody: Boolean = false,
    val itemBuildList: List<ItemData> = listOf()
)

sealed interface ItemHomeAction {
    data object RefreshItemData : ItemHomeAction
    data object ToggleSelectNewItem : ItemHomeAction

    data class ToggleItemTagType(val itemTagType: ItemTagType) : ItemHomeAction
    data class ChangeMapTypeFilter(val mapType: MapType) : ItemHomeAction
    data class ChangeItemSearchKeyword(val searchKeyword: String) : ItemHomeAction
    data class SelectItemData(val itemDataId: String?) : ItemHomeAction
    data class AddItemBuild(val itemData: ItemData) : ItemHomeAction
    data class DeleteItemBuild(val index: Int) : ItemHomeAction

    data object ToggleVisibleFilterBody : ItemHomeAction
    data object ToggleVisibleBuildBody : ItemHomeAction
}

sealed interface ItemHomeSideEffect {
    data class ShowErrorMessage(val exception: Exception) : ItemHomeSideEffect
}