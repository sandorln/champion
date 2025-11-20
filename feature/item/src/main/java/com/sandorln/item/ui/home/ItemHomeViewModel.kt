package com.sandorln.item.ui.home

import android.graphics.Bitmap
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
import com.sandorln.item.model.ItemBuildException
import com.sandorln.item.util.getStatusList
import com.sandorln.item.util.getUniqueStatusList
import com.sandorln.model.data.image.SpriteType
import com.sandorln.model.data.item.ItemData
import com.sandorln.model.data.map.MapType
import com.sandorln.model.data.patchnote.PatchNoteData
import com.sandorln.model.type.ItemTagType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemHomeViewModel @Inject constructor(
    getItemListByCurrentVersion: GetItemListByCurrentVersion,
    getNewItemIdListByCurrentVersion: GetNewItemIdListByCurrentVersion,
    getSpriteBitmapByCurrentVersion: GetSpriteBitmapByCurrentVersion,
    getCurrentVersionDistinctBySpriteType: GetCurrentVersionDistinctBySpriteType,
    getCurrentVersion: GetCurrentVersion,
    private val getItemPatchNoteList: GetItemPatchNoteList,
    private val refreshDownloadSpriteBitmap: RefreshDownloadSpriteBitmap
) : ViewModel() {
    companion object {
        const val ITEM_BUILD_MAX_COUNT = 6
        const val ITEM_LEGEND_DEPTH = 3
        private val SUPPORT_ITEM_ID_LIST = listOf("3869", "3870", "3871", "3876", "3877", "4643", "4638") // 서폿 아이템 ID
    }

    private val _itemUiState = MutableStateFlow(ItemHomeUiState())
    val itemUiState = _itemUiState.asStateFlow()

    private var _latestItemDataList: List<ItemData> = listOf()
    private val _searchKeyword = MutableStateFlow("")
    private val _span = MutableStateFlow(1)

    fun sendAction(action: ItemHomeAction) {
        when (action) {
            is ItemHomeAction.ChangeItemSearchKeyword -> _searchKeyword.update { action.searchKeyword }
            is ItemHomeAction.ChangeSpan -> _span.update { action.span }
            is ItemHomeAction.ChangeMapTypeFilter -> _itemUiState.update { it.copy(selectMapType = action.mapType) }
            is ItemHomeAction.SelectItemData -> _itemUiState.update { it.copy(selectedItemId = action.itemDataId) }
            is ItemHomeAction.ToggleItemTagType -> _itemUiState.update { currentUiState ->
                val isSelected = currentUiState.selectTag.contains(action.itemTagType)
                val selectTag = currentUiState.selectTag.toMutableSet()
                if (isSelected) {
                    selectTag.remove(action.itemTagType)
                } else {
                    selectTag.add(action.itemTagType)
                }
                currentUiState.copy(selectTag = selectTag)
            }

            ItemHomeAction.RefreshItemData -> refreshItemData()
            ItemHomeAction.ToggleSelectNewItem -> _itemUiState.update { it.copy(isSelectNewItem = !it.isSelectNewItem) }
            is ItemHomeAction.ChangeShowFilterDialog -> _itemUiState.update { it.copy(isShowFilterDialog = action.isVisible) }
            is ItemHomeAction.AddItemBuild -> addItemBuild(action.itemData)
            is ItemHomeAction.DeleteItemBuild -> deletedItemBuildByIndex(action.index)
        }
    }

    private fun addItemBuild(addItemData: ItemData) {
        val itemBuildList = _itemUiState.value.itemBuildList
        val shouldAddItemBuildList = itemBuildList.size < ITEM_BUILD_MAX_COUNT
        val hasSameLegendItem = addItemData.depth >= ITEM_LEGEND_DEPTH && itemBuildList.any { it.id == addItemData.id }

        when {
            !shouldAddItemBuildList -> sendSideEffect(ItemHomeSideEffect.ShowErrorMessage(ItemBuildException.MaxItemSizeReached()))
            hasSameLegendItem -> sendSideEffect(ItemHomeSideEffect.ShowErrorMessage(ItemBuildException.DuplicateLegendaryItem()))

            else -> {
                val tempUiState = _itemUiState.value.copy()
                val itemBuildList = tempUiState
                    .itemBuildList
                    .toMutableList()
                    .apply { add(addItemData) }

                refreshItemBuild(itemBuildList)
                sendSideEffect(ItemHomeSideEffect.SuccessItemBuild())
            }
        }
    }

    private fun deletedItemBuildByIndex(index: Int) {
        val tempUiState = _itemUiState.value.copy()
        val itemBuildList = runCatching {
            tempUiState
                .itemBuildList
                .toMutableList()
                .apply { removeAt(index) }
        }.onFailure {
            sendSideEffect(ItemHomeSideEffect.ShowErrorMessage(it as Exception))
        }.getOrNull()

        if (itemBuildList == null) return
        refreshItemBuild(itemBuildList)
    }

    private fun refreshItemBuild(itemBuildList: List<ItemData> = emptyList()) {
        val itemBuildStatus: MutableMap<String, Pair<Int, String>> = mutableMapOf()
        itemBuildList
            .map(ItemData::getStatusList)
            .forEach { itemStatusList ->
                itemStatusList.forEach { (title, value, suffix) ->
                    val defaultStatus = itemBuildStatus[title + suffix] ?: Pair(0, "")
                    val sumValue = defaultStatus.first + value
                    itemBuildStatus[title + suffix] = sumValue to suffix
                }
            }
        itemBuildStatus.toSortedMap()

        val itemBuildUniqueList: List<Pair<String, String>> = itemBuildList
            .map(ItemData::getUniqueStatusList)
            .distinctBy { it.first }
            .filter { it.second.isNotEmpty() }

        val itemBuildTotalGold: Int = itemBuildList.sumOf { itemData -> itemData.gold.total }

        _itemUiState.update {
            it.copy(
                itemBuildList = itemBuildList,
                itemBuildStatus = itemBuildStatus,
                itemBuildUniqueList = itemBuildUniqueList,
                itemBuildTotalGold = itemBuildTotalGold
            )
        }
    }

    private val _sideEffect = MutableSharedFlow<ItemHomeSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()
    private fun sendSideEffect(sideEffect: ItemHomeSideEffect) {
        viewModelScope.launch { _sideEffect.emit(sideEffect) }
    }

    private var _refreshJob: Job? = null
    private fun refreshItemData() {
        _refreshJob?.cancel()

        _refreshJob = viewModelScope.launch {
            val currentVersionName = _itemUiState.value.currentVersionName
            _itemUiState.update {
                it.copy(
                    isLoading = true,
                    itemPatchList = null,
                    itemBuildList = emptyList(),
                    itemBuildStatus = emptyMap(),
                    itemBuildUniqueList = emptyList(),
                    itemBuildTotalGold = 0
                )
            }

            val spriteFileList = _latestItemDataList.map { item -> item.image.sprite }.distinct()
            refreshDownloadSpriteBitmap.invoke(
                spriteType = SpriteType.Item,
                fileNameList = spriteFileList
            ).onFailure {
                _sideEffect.emit(ItemHomeSideEffect.ShowErrorMessage(it as Exception))
            }

            val itemPatchNoteList = getItemPatchNoteList.invoke(currentVersionName).getOrNull() ?: emptyList()
            _itemUiState.update {
                it.copy(
                    isLoading = false,
                    itemPatchList = itemPatchNoteList
                )
            }
        }
    }

    init {
        viewModelScope.launch {
            launch {
                combine(
                    getItemListByCurrentVersion.invoke(),
                    getNewItemIdListByCurrentVersion.invoke(),
                    _searchKeyword,
                    _itemUiState.map { it.isSelectNewItem }.distinctUntilChanged(),
                    _itemUiState.map { it.selectMapType }.distinctUntilChanged(),
                    _itemUiState.map { it.selectTag }.distinctUntilChanged(),
                ) { values ->
                    runCatching {
                        val itemList = values[0] as List<ItemData>
                        val newItemIdList = values[1] as List<String>
                        val searchKeyword = values[2] as String
                        val isSelectNewItem = values[3] as Boolean
                        val selectMapType = values[4] as MapType
                        val selectTag = values[5] as Set<ItemTagType>

                        _latestItemDataList = itemList
                        val itemListIdMap = itemList.associateBy(ItemData::id)

                        val filterItemList = if (isSelectNewItem) {
                            itemList.fastFilter { newItemIdList.contains(it.id) }
                        } else {
                            itemList
                        }

                        filterItemList.filter { item ->
                            val isMutationItem = item.gold.total == 0 && item.gold.sell == 0
                            if (isMutationItem) return@filter false

                            /* Tag Type Filter */
                            when {
                                selectTag.isEmpty() -> {}
                                !item.tags.containsAll(selectTag) -> return@filter false
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
                            } else {
                                map { itemData -> itemData.copy(depth = 1) }
                            }
                        }
                    }.getOrDefault(emptyList<ItemData>())
                }.combine(_span) { itemDataList, span ->
                    val (bootItemList, notBootItemList) = itemDataList.partition { it.tags.contains(ItemTagType.Boots) }
                    val (consumableItemList, notConsumableItemList) = notBootItemList.partition { it.tags.contains(ItemTagType.Consumable) && it.depth < ItemHomeViewModel.ITEM_LEGEND_DEPTH }
                    val (normalItemList, notNormalItemList) = notConsumableItemList.partition { it.depth < 2 }
                    val (epicItemList, notEpicItemList) = notNormalItemList.partition { it.depth < ItemHomeViewModel.ITEM_LEGEND_DEPTH }
                    val (orrnItemList, legendItemList) = notEpicItemList.partition { it.depth == Int.MAX_VALUE }

                    runCatching {
                        _itemUiState.update {
                            it.copy(
                                bootItemList = bootItemList.chunked(span),
                                consumableItemList = consumableItemList.chunked(span),
                                normalItemList = normalItemList.chunked(span),
                                epicItemList = epicItemList.chunked(span),
                                orrnItemList = orrnItemList.chunked(span),
                                legendItemList = legendItemList.chunked(span)
                            )
                        }
                    }.onFailure {
                        _sideEffect.emit(ItemHomeSideEffect.ShowErrorMessage(it as Exception))
                    }
                }.flowOn(Dispatchers.Default).collect()
            }

            launch {
                getCurrentVersion
                    .invoke()
                    .map { it.name }
                    .distinctUntilChanged()
                    .collectLatest { version ->
                        _refreshJob?.cancel()

                        _itemUiState.update {
                            it.copy(
                                itemBuildList = emptyList(),
                                itemBuildStatus = emptyMap(),
                                itemBuildUniqueList = emptyList(),
                                itemBuildTotalGold = 0,
                                currentVersionName = version,
                                isLoading = true,
                                itemPatchList = null,
                            )
                        }

                        val itemPatchNoteList = getItemPatchNoteList.invoke(version).getOrNull() ?: emptyList()
                        _itemUiState.update {
                            it.copy(
                                isLoading = false,
                                itemPatchList = itemPatchNoteList
                            )
                        }
                    }
            }

            launch {
                getSpriteBitmapByCurrentVersion
                    .invoke(SpriteType.Item)
                    .collectLatest { currentSpriteMap ->
                        _itemUiState.update {
                            it.copy(currentSpriteMap = currentSpriteMap)
                        }
                    }
            }

            launch {
                combine(
                    getCurrentVersionDistinctBySpriteType.invoke(SpriteType.Item),
                    getItemListByCurrentVersion.invoke()
                ) { version, itemList ->
                    if (version.isDownLoadItemIconSprite || itemList.isEmpty())
                        return@combine null

                    itemList.map { item -> item.image.sprite }.distinct()
                }.filterNotNull()
                    .collectLatest { spriteFileList ->
                        refreshDownloadSpriteBitmap
                            .invoke(
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
    val itemPatchList: List<PatchNoteData>? = null,

    val itemBuildList: List<ItemData> = listOf(),
    val itemBuildStatus: Map<String, Pair<Int, String>> = emptyMap(),
    val itemBuildUniqueList: List<Pair<String, String>> = emptyList(),
    val itemBuildTotalGold: Int = 0,

    val currentVersionName: String = "",

    val bootItemList: List<List<ItemData>> = listOf(),
    val consumableItemList: List<List<ItemData>> = listOf(),
    val normalItemList: List<List<ItemData>> = listOf(),
    val epicItemList: List<List<ItemData>> = listOf(),
    val orrnItemList: List<List<ItemData>> = listOf(),
    val legendItemList: List<List<ItemData>> = listOf(),

    val currentSpriteMap: Map<String, Bitmap> = emptyMap(),

    val selectMapType: MapType = MapType.SUMMONER_RIFT,
    val selectTag: Set<ItemTagType> = emptySet(),
    val isSelectNewItem: Boolean = false,
    val selectedItemId: String? = null,

    val isShowFilterDialog: Boolean = false
)

sealed interface ItemHomeAction {
    data object RefreshItemData : ItemHomeAction
    data object ToggleSelectNewItem : ItemHomeAction
    data class ToggleItemTagType(val itemTagType: ItemTagType) : ItemHomeAction
    data class ChangeMapTypeFilter(val mapType: MapType) : ItemHomeAction
    data class SelectItemData(val itemDataId: String?) : ItemHomeAction
    data class ChangeItemSearchKeyword(val searchKeyword: String) : ItemHomeAction
    data class ChangeSpan(val span: Int) : ItemHomeAction
    data class ChangeShowFilterDialog(val isVisible: Boolean) : ItemHomeAction
    data class AddItemBuild(val itemData: ItemData) : ItemHomeAction
    data class DeleteItemBuild(val index: Int) : ItemHomeAction
}

sealed interface ItemHomeSideEffect {
    class SuccessItemBuild : ItemHomeSideEffect
    data class ShowMessage(val message: String) : ItemHomeSideEffect
    data class ShowErrorMessage(val exception: Exception) : ItemHomeSideEffect
}