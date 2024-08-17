package com.sandorln.item.ui.home

import androidx.compose.ui.util.fastFilter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.domain.usecase.item.GetItemListByCurrentVersion
import com.sandorln.domain.usecase.item.GetItemPatchNoteList
import com.sandorln.domain.usecase.item.GetNewItemIdListByCurrentVersion
import com.sandorln.domain.usecase.sprite.GetCurrentVersionDistinctBySpriteType
import com.sandorln.domain.usecase.sprite.GetSpriteBitmapByCurrentVersion
import com.sandorln.domain.usecase.sprite.RefreshDownloadSpriteBitmap
import com.sandorln.domain.usecase.version.GetCurrentVersion
import com.sandorln.item.R
import com.sandorln.item.model.ItemBuildException
import com.sandorln.item.util.getStatusList
import com.sandorln.item.util.getUniqueStatusList
import com.sandorln.model.data.image.SpriteType
import com.sandorln.model.data.item.ItemData
import com.sandorln.model.data.map.MapType
import com.sandorln.model.data.patchnote.PatchNoteData
import com.sandorln.model.data.version.Version
import com.sandorln.model.type.ItemTagType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
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
    getItemPatchNoteList: GetItemPatchNoteList,
    getCurrentVersion: GetCurrentVersion
) : ViewModel() {
    companion object {
        const val ITEM_BUILD_MAX_COUNT = 6
        const val ITEM_LEGEND_DEPTH = 3
        private val SUPPORT_ITEM_ID_LIST = listOf("3869", "3870", "3871", "3876", "3877", "4643", "4638") // 서폿 아이템 ID
    }

    val currentVersion = getCurrentVersion.invoke().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Version())

    private val _itemUiState = MutableStateFlow(ItemHomeUiState())
    val itemUiState = _itemUiState.asStateFlow()
    private val _itemBuildList: StateFlow<List<ItemData>> = _itemUiState
        .distinctUntilChangedBy { it.itemBuildList }
        .map { it.itemBuildList }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    val itemBuildStatus = _itemBuildList
        .map { itemDataList ->
            val totalStatus: MutableMap<String, Pair<Int, String>> = mutableMapOf()

            itemDataList
                .map(ItemData::getStatusList)
                .forEach { itemStatusList ->
                    itemStatusList.forEach { (title, value, suffix) ->
                        val defaultStatus = totalStatus[title + suffix] ?: Pair(0, "")
                        val sumValue = defaultStatus.first + value
                        totalStatus[title + suffix] = sumValue to suffix
                    }
                }

            totalStatus.toSortedMap()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyMap())

    val itemBuildUniqueList = _itemBuildList
        .map { itemDataList ->
            itemDataList
                .map(ItemData::getUniqueStatusList)
                .distinctBy { it.first }
                .filter { it.second.isNotEmpty() }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val itemBuildGold = _itemBuildList
        .map { itemBuildList -> itemBuildList.sumOf { itemData -> itemData.gold.total } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    private val _itemAction = MutableSharedFlow<ItemHomeAction>()
    fun sendAction(itemHomeAction: ItemHomeAction) = viewModelScope.launch {
        _itemAction.emit(itemHomeAction)
    }

    private val _sideEffect = MutableSharedFlow<ItemHomeSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    private val _itemFilterTransform: suspend (ItemHomeUiState, List<ItemData>, List<String>) -> List<ItemData> = { uiState, itemList, newItemIdList ->
        val itemListIdMap = itemList.associateBy(ItemData::id)
        val searchKeyword = uiState.searchKeyword
        val selectMapType = uiState.selectMapType
        val filterItemList = if (uiState.isSelectNewItem) {
            itemList.fastFilter { newItemIdList.contains(it.id) }
        } else {
            itemList
        }

        filterItemList.filter { item ->
            val isMutationItem = item.gold.total == 0 && item.gold.sell == 0
            if (isMutationItem) return@filter false

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
        }.run {
            if (selectMapType == MapType.ARAM || selectMapType == MapType.SUMMONER_RIFT) {
                map { itemData ->
                    if (itemData.depth == 0 || itemData.tags.contains(ItemTagType.Consumable)) return@map itemData

                    val firstIntoItem = itemListIdMap[itemData.into.firstOrNull()]
                    val firstFromItem = itemListIdMap[itemData.from.firstOrNull()]

                    val isPreOrnnItem = itemData.into.size == 1 && (firstIntoItem?.gold?.total ?: 0) == itemData.gold.total
                    val isNotOrrnItem = SUPPORT_ITEM_ID_LIST.none { it == itemData.id }
                    val isOrnnItem = itemData.from.size == 1 && (firstFromItem?.gold?.total ?: 0) == itemData.gold.total && isNotOrrnItem
                    val isLegendItem = itemData.into.isEmpty()

                    when {
                        isPreOrnnItem -> itemData.copy(depth = ITEM_LEGEND_DEPTH)
                        isOrnnItem -> itemData.copy(depth = Int.MAX_VALUE)
                        isLegendItem -> itemData.copy(depth = ITEM_LEGEND_DEPTH)
                        else -> itemData
                    }
                }
            } else
                map { itemData -> itemData.copy(depth = 1) }
        }
    }

    private val _itemMutex = Mutex()

    private val _currentItemList = getItemListByCurrentVersion.invoke().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _currentNewItemIdList = getNewItemIdListByCurrentVersion.invoke().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val currentSpriteMap = getSpriteBitmapByCurrentVersion.invoke(SpriteType.Item).stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyMap())

    val displayItemList = combine(_itemUiState, _currentItemList, _currentNewItemIdList, _itemFilterTransform)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    init {
        viewModelScope.launch {
            launch {
                currentVersion
                    .map { it.name }
                    .distinctUntilChanged()
                    .collectLatest { version ->
                        _itemMutex.withLock {
                            _itemUiState.update {
                                it.copy(
                                    itemPatchList = null,
                                    itemBuildList = emptyList()
                                )
                            }

                            val itemPatchNoteList = getItemPatchNoteList.invoke(version).getOrNull() ?: emptyList()
                            _itemUiState.update {
                                it.copy(itemPatchList = itemPatchNoteList)
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
                                _itemUiState.update {
                                    it.copy(
                                        isLoading = true,
                                        itemPatchList = null
                                    )
                                }

                                val spriteFileList = _currentItemList.value.map { item -> item.image.sprite }.distinct()
                                refreshDownloadSpriteBitmap.invoke(
                                    spriteType = SpriteType.Item,
                                    fileNameList = spriteFileList
                                ).onFailure {
                                    _sideEffect.emit(ItemHomeSideEffect.ShowErrorMessage(it as Exception))
                                }

                                val itemPatchNoteList = getItemPatchNoteList.invoke(currentVersion.value.name).getOrNull() ?: emptyList()
                                _itemUiState.update {
                                    it.copy(
                                        isLoading = false,
                                        itemPatchList = itemPatchNoteList
                                    )
                                }
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

                            is ItemHomeAction.AddItemBuild -> {
                                val itemBuildList = _itemUiState.value.itemBuildList
                                val addItemData = action.itemData
                                val shouldAddItemBuildList = itemBuildList.size < ITEM_BUILD_MAX_COUNT
                                val hasSameLegendItem = addItemData.depth >= ITEM_LEGEND_DEPTH && itemBuildList.any { it.id == addItemData.id }

                                when {
                                    !shouldAddItemBuildList -> {
                                        _sideEffect.emit(ItemHomeSideEffect.ShowErrorMessage(ItemBuildException.NotShouldAddItemSize))
                                    }

                                    hasSameLegendItem -> {
                                        _sideEffect.emit(ItemHomeSideEffect.ShowErrorMessage(ItemBuildException.NotAddSameLegendItem))
                                    }

                                    else -> {
                                        _itemUiState.update { uiState ->
                                            val itemBuildSet = uiState
                                                .itemBuildList
                                                .toMutableList()
                                                .apply {
                                                    add(addItemData)
                                                }

                                            uiState.copy(itemBuildList = itemBuildSet)
                                        }
                                        _sideEffect.emit(
                                            ItemHomeSideEffect.ShowMessage(R.string.item_build_success)
                                        )
                                    }
                                }
                            }

                            is ItemHomeAction.DeleteItemBuild -> {
                                runCatching {
                                    _itemUiState.update { uiState ->
                                        val itemBuildList = uiState
                                            .itemBuildList
                                            .toMutableList()
                                            .apply { removeAt(action.index) }

                                        uiState.copy(itemBuildList = itemBuildList)
                                    }
                                }
                            }

                            is ItemHomeAction.ChangeShowFilterDialog -> {
                                _itemUiState.update { it.copy(isShowFilterDialog = action.isVisible) }
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
    val itemBuildList: List<ItemData> = listOf(),
    val itemPatchList: List<PatchNoteData>? = null,
    val isShowFilterDialog: Boolean = false
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

    data class ChangeShowFilterDialog(val isVisible: Boolean) : ItemHomeAction
}

sealed interface ItemHomeSideEffect {
    data class ShowMessage(val stringId: Int) : ItemHomeSideEffect
    data class ShowErrorMessage(val exception: Exception) : ItemHomeSideEffect
}