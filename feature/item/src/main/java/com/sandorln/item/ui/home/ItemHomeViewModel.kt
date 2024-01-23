package com.sandorln.item.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.data.repository.item.ItemRepository
import com.sandorln.data.repository.sprite.SpriteRepository
import com.sandorln.data.repository.version.VersionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
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
    val currentSpriteMap = spriteRepository.currentSpriteFileMap.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyMap())
    private val _itemMutex = Mutex()

    private val _itemUiState = MutableStateFlow(ItemHomeUiState())
    val itemUiState = _itemUiState.asStateFlow()
    val displayItemList = combine(_itemUiState, _currentItemList) { uiState, itemList ->
        val searchKeyword = uiState.searchKeyword

        itemList.filter { item ->
            item.name.startsWith(searchKeyword)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _itemAction = MutableSharedFlow<ItemHomeAction>()
    fun sendAction(itemHomeAction: ItemHomeAction) = viewModelScope.launch {
        _itemAction.emit(itemHomeAction)
    }

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
                        }
                    }
                }
            }

            launch(Dispatchers.IO) {
                combine(versionRepository.currentVersion, _currentItemList) { version, itemList ->
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
    val searchKeyword: String = ""
)

sealed interface ItemHomeAction {
    data object RefreshItemData : ItemHomeAction
    data class ChangeItemSearchKeyword(val searchKeyword: String) : ItemHomeAction
}