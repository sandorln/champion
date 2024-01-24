package com.sandorln.item.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.data.repository.item.ItemRepository
import com.sandorln.data.repository.sprite.SpriteRepository
import com.sandorln.data.repository.version.VersionRepository
import com.sandorln.model.data.item.ItemData
import com.sandorln.model.data.map.MapType
import com.sandorln.model.type.ItemTagType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class ItemHomeViewModel @Inject constructor(
    versionRepository: VersionRepository,
    itemRepository: ItemRepository,
    spriteRepository: SpriteRepository
) : ViewModel() {
    private val _currentItemList = itemRepository.currentItemList.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _currentNewItemList = itemRepository.currentNewItemList.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    val currentSpriteMap = spriteRepository.currentSpriteFileMap.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyMap())
    private val _itemMutex = Mutex()

    private val _itemUiState = MutableStateFlow(ItemHomeUiState())
    val itemUiState = _itemUiState.asStateFlow()

    private val _itemAction = MutableSharedFlow<ItemHomeAction>()
    fun sendAction(itemHomeAction: ItemHomeAction) = viewModelScope.launch {
        _itemAction.emit(itemHomeAction)
    }

    private val _itemFilter: suspend (ItemHomeUiState, List<ItemData>, List<ItemData>) -> List<ItemData> = { uiState, itemList, newItemList ->
        val searchKeyword = uiState.searchKeyword
        val selectMapType = uiState.isSelectMapType
        val filterItemList = if (uiState.isSelectNewItem) newItemList else itemList

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
    val displayItemList = combine(_itemUiState, _currentItemList, _currentNewItemList, _itemFilter)
        .flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    init {
        viewModelScope.launch {
            launch {
                _itemAction.collect { action ->
                    _itemMutex.withLock {
                        val currentUiState = _itemUiState.value
                        when (action) {
                            is ItemHomeAction.RefreshItemData -> {
                                /* TODO :: 아이템 목록 / 아이콘 갱신 */
                                _itemUiState.emit(currentUiState.copy(isLoading = true))
                                delay(2000)
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
                        }
                    }
                }
            }

            launch(Dispatchers.IO) {
                combine(
                    versionRepository.currentVersion.distinctUntilChangedBy { setOf(it.isDownLoadItemIconSprite, it.name) },
                    _currentItemList
                ) { version, itemList ->
                    if (version.isDownLoadItemIconSprite || itemList.isEmpty())
                        return@combine

                    val spriteFileList = itemList.map { it.image.sprite }.distinct()
                    val spriteResult = spriteRepository.refreshSpriteBitmap(version.name, spriteFileList).isSuccess

                    val tempVersion = version.copy(isDownLoadChampionIconSprite = spriteResult)
                    versionRepository.updateVersionData(tempVersion)
                }.collect()
            }
        }
    }
}

data class ItemHomeUiState(
    val isLoading: Boolean = false,
    val searchKeyword: String = "",
    val isSelectMapType: MapType = MapType.ALL,
    val selectTag: Set<ItemTagType> = emptySet(),
    val isSelectNewItem: Boolean = false
)

sealed interface ItemHomeAction {
    data object RefreshItemData : ItemHomeAction
    data object ToggleSelectNewItem : ItemHomeAction

    data class ToggleItemTagType(val itemTagType: ItemTagType) : ItemHomeAction
    data class ChangeMapTypeFilter(val mapType: MapType) : ItemHomeAction
    data class ChangeItemSearchKeyword(val searchKeyword: String) : ItemHomeAction
}