package com.sandorln.item.ui.builder.edit

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.domain.usecase.item.GetItemListByCurrentVersion
import com.sandorln.domain.usecase.itembuild.GetItemBuildById
import com.sandorln.domain.usecase.itembuild.SaveItemBuild
import com.sandorln.domain.usecase.sprite.GetCurrentVersionDistinctBySpriteType
import com.sandorln.domain.usecase.sprite.GetSpriteBitmapByCurrentVersion
import com.sandorln.domain.usecase.version.GetCurrentVersion
import com.sandorln.item.R
import com.sandorln.model.data.image.SpriteType
import com.sandorln.model.data.item.ItemBuild
import com.sandorln.model.data.item.ItemData
import com.sandorln.model.data.map.MapType
import com.sandorln.model.type.ChampionTag
import com.sandorln.model.type.ItemTagType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class ItemBuilderEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getItemBuildById: GetItemBuildById,
    private val saveItemBuild: SaveItemBuild,
    getCurrentVersion: GetCurrentVersion,
    getItemListByCurrentVersion: GetItemListByCurrentVersion,
    getSpriteBitmapByCurrentVersion: GetSpriteBitmapByCurrentVersion,
    getCurrentVersionDistinctBySpriteType: GetCurrentVersionDistinctBySpriteType
) : ViewModel() {

    companion object {
        const val ITEM_BUILD_MAX_COUNT = 6
        const val ITEM_LEGEND_DEPTH = 3
        private val SUPPORT_ITEM_ID_LIST = listOf("3869", "3870", "3871", "3876", "3877", "4643", "4638")
    }

    private val buildId: Long = savedStateHandle.get<Long>("buildId") ?: 0L

    private val _uiState = MutableStateFlow(ItemBuilderEditUiState(id = buildId))
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<ItemBuilderEditSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    private var _latestItemDataList: List<ItemData> = emptyList()
    private val _searchKeyword = MutableStateFlow("")
    private val _span = MutableStateFlow(5)
    private var _isBuildLoaded = false

    init {
        // Collect current version
        viewModelScope.launch {
            getCurrentVersion
                .invoke()
                .map { it.name }
                .distinctUntilChanged()
                .collectLatest { version ->
                    _uiState.update { it.copy(currentVersionName = version) }
                }
        }

        // Collect sprites
        viewModelScope.launch {
            getCurrentVersionDistinctBySpriteType
                .invoke(SpriteType.Item)
                .filterNotNull()
                .flatMapLatest {
                    getSpriteBitmapByCurrentVersion.invoke(SpriteType.Item)
                }
                .flowOn(Dispatchers.IO)
                .collectLatest { spriteBitmapMap ->
                    _uiState.update { it.copy(currentSpriteMap = spriteBitmapMap) }
                }
        }

        // Collect items and filter catalog
        viewModelScope.launch {
            combine(
                getItemListByCurrentVersion.invoke(),
                _searchKeyword
            ) { itemList, searchKeyword ->
                _latestItemDataList = itemList

                // Load existing build items once itemList is available
                if (buildId > 0L && !_isBuildLoaded) {
                    val existingBuild = getItemBuildById(buildId).firstOrNull()
                    if (existingBuild != null) {
                        val itemMap = itemList.associateBy { it.id }
                        val selectedItems = existingBuild.itemIdList.mapNotNull { itemMap[it] }
                        _uiState.update {
                            it.copy(
                                title = existingBuild.title,
                                selectedPositions = existingBuild.positionList.toSet(),
                                selectedItems = selectedItems
                            )
                        }
                        _isBuildLoaded = true
                    }
                }

                val itemListIdMap = itemList.associateBy(ItemData::id)
                val filterItemList = itemList.filter { item ->
                    val isMutationItem = item.gold.total == 0 && item.gold.sell == 0
                    if (isMutationItem) return@filter false
                    if (!item.inStore) return@filter false
                    item.name.contains(searchKeyword)
                }.groupBy { it.name }
                    .map { (_, items) ->
                        if (items.size == 1) {
                            items.first()
                        } else {
                            items.minWithOrNull(
                                compareBy<ItemData> { it.id.length > 4 }
                                    .thenBy { it.mapType != MapType.ALL }
                                    .thenBy { it.id }
                            ) ?: items.first()
                        }
                    }.run {
                        val version = itemList.firstOrNull()?.version ?: ""
                        val isAfterOrnnRemovedVersion = runCatching {
                            val v = version.split('.').map { it.toIntOrNull() ?: 0 }
                            v[0] > 14 || (v[0] == 14 && v.getOrElse(1) { 0 } >= 13)
                        }.getOrDefault(false)

                        map { itemData ->
                            if (itemData.depth == 0 || itemData.tags.contains(ItemTagType.Consumable)) return@map itemData

                            val firstIntoItem = itemListIdMap[itemData.into.firstOrNull()]
                            val firstFromItem = itemListIdMap[itemData.from.firstOrNull()]

                            val isPreOrnnItem = !isAfterOrnnRemovedVersion && itemData.into.size == 1 && (firstIntoItem?.gold?.total ?: 0) == itemData.gold.total
                            val isNotOrrnItem = SUPPORT_ITEM_ID_LIST.none { it == itemData.id }
                            val isOrnnItem = !isAfterOrnnRemovedVersion && itemData.from.size == 1 && (firstFromItem?.gold?.total ?: 0) == itemData.gold.total && isNotOrrnItem
                            val isLegendItem = itemData.into.isEmpty()

                            when {
                                isPreOrnnItem -> itemData.copy(depth = ITEM_LEGEND_DEPTH)
                                isOrnnItem -> itemData.copy(depth = Int.MAX_VALUE)
                                isLegendItem -> itemData.copy(depth = ITEM_LEGEND_DEPTH)
                                else -> itemData
                            }
                        }
                    }
                filterItemList
            }.combine(_span) { itemDataList, span ->
                val (bootItemList, notBootItemList) = itemDataList.partition { it.tags.contains(ItemTagType.Boots) }
                val (consumableItemList, notConsumableItemList) = notBootItemList.partition { it.tags.contains(ItemTagType.Consumable) && it.depth < ITEM_LEGEND_DEPTH }
                val (normalItemList, notNormalItemList) = notConsumableItemList.partition { it.depth < 2 }
                val (epicItemList, notEpicItemList) = notNormalItemList.partition { it.depth < ITEM_LEGEND_DEPTH }
                val (orrnItemList, legendItemList) = notEpicItemList.partition { it.depth == Int.MAX_VALUE }

                val safeSpan = span.coerceAtLeast(1)
                _uiState.update {
                    it.copy(
                        bootItemList = bootItemList.chunked(safeSpan),
                        consumableItemList = consumableItemList.chunked(safeSpan),
                        normalItemList = normalItemList.chunked(safeSpan),
                        epicItemList = epicItemList.chunked(safeSpan),
                        orrnItemList = orrnItemList.chunked(safeSpan),
                        legendItemList = legendItemList.chunked(safeSpan)
                    )
                }
            }.flowOn(Dispatchers.Default).collect()
        }
    }

    fun sendAction(action: ItemBuilderEditAction) {
        when (action) {
            is ItemBuilderEditAction.ChangeTitle -> _uiState.update { it.copy(title = action.title) }
            is ItemBuilderEditAction.TogglePosition -> _uiState.update { current ->
                val newPositions = current.selectedPositions.toMutableSet()
                if (newPositions.contains(action.position)) {
                    newPositions.remove(action.position)
                } else {
                    newPositions.add(action.position)
                }
                current.copy(selectedPositions = newPositions)
            }
            is ItemBuilderEditAction.ChangeSearchKeyword -> {
                _searchKeyword.update { action.searchKeyword }
                _uiState.update { it.copy(searchKeyword = action.searchKeyword) }
            }
            is ItemBuilderEditAction.ChangeSpan -> _span.update { action.span }
            is ItemBuilderEditAction.SelectItemId -> _uiState.update { it.copy(selectedItemId = action.itemDataId) }
            is ItemBuilderEditAction.AddItem -> addItem(action.itemData)
            is ItemBuilderEditAction.RemoveItemByIndex -> removeItemByIndex(action.index)
            ItemBuilderEditAction.Save -> saveBuild()
        }
    }

    private fun addItem(itemData: ItemData) {
        val currentList = _uiState.value.selectedItems
        if (currentList.size >= ITEM_BUILD_MAX_COUNT) {
            viewModelScope.launch {
                _sideEffect.emit(ItemBuilderEditSideEffect.ShowError(R.string.item_build_should_add_error))
            }
            return
        }
        val hasSameLegendItem = itemData.depth >= ITEM_LEGEND_DEPTH && currentList.any { it.id == itemData.id }
        if (hasSameLegendItem) {
            viewModelScope.launch {
                _sideEffect.emit(ItemBuilderEditSideEffect.ShowError(R.string.item_build_same_legend_error))
            }
            return
        }
        _uiState.update {
            it.copy(
                selectedItems = currentList + itemData,
                selectedItemId = null
            )
        }
    }

    private fun removeItemByIndex(index: Int) {
        val currentList = _uiState.value.selectedItems.toMutableList()
        if (index in currentList.indices) {
            currentList.removeAt(index)
            _uiState.update { it.copy(selectedItems = currentList) }
        }
    }

    private fun saveBuild() {
        val currentState = _uiState.value
        if (!currentState.canSave) return
        viewModelScope.launch {
            val itemBuild = ItemBuild(
                id = currentState.id,
                version = currentState.currentVersionName,
                title = currentState.title.trim(),
                positionList = currentState.selectedPositions.toList(),
                itemIdList = currentState.selectedItems.map { it.id }
            )
            val result = saveItemBuild(itemBuild)
            result.onSuccess {
                _sideEffect.emit(ItemBuilderEditSideEffect.SaveSuccess)
            }.onFailure { e ->
                val errorMsg = e.message ?: "저장에 실패했습니다."
                _sideEffect.emit(ItemBuilderEditSideEffect.ShowToast(errorMsg))
            }
        }
    }
}
