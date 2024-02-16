package com.sandorln.item.ui.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.domain.usecase.item.GetItemCombination
import com.sandorln.domain.usecase.item.GetItemDataByItemId
import com.sandorln.domain.usecase.sprite.GetSpriteBitmapByCurrentVersion
import com.sandorln.model.data.image.SpriteType
import com.sandorln.model.data.item.ItemCombination
import com.sandorln.model.data.item.ItemData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class ItemDetailDialogViewModel @Inject constructor(
    getSpriteBitmapByCurrentVersion: GetSpriteBitmapByCurrentVersion,
    private val getItemDataByItemId: GetItemDataByItemId,
    private val getItemCombination: GetItemCombination
) : ViewModel() {
    val currentSpriteMap = getSpriteBitmapByCurrentVersion.invoke(SpriteType.Item).stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyMap())
    fun initSetIdAndVersion(id: String, version: String) = viewModelScope.launch(Dispatchers.IO) {
        _uiMutex.withLock {
            changeData(id, version)
        }
    }

    private val _uiMutex = Mutex()
    private val _uiState = MutableStateFlow(ItemDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val _action = MutableSharedFlow<ItemDetailAction>()
    fun sendAction(action: ItemDetailAction) = viewModelScope.launch { _action.emit(action) }

    private suspend fun changeData(
        id: String,
        version: String,
    ) {
        val itemData = getItemDataByItemId.invoke(id, version).getOrDefault(ItemData())
        val itemCombination = getItemCombination.invoke(id, version).getOrDefault(ItemCombination())

        _uiState.update {
            it.copy(
                selectedId = id,
                version = version,
                itemData = itemData,
                itemCombination = itemCombination
            )
        }
    }

    init {
        viewModelScope.launch {
            launch(Dispatchers.IO) {
                _action.collect { action ->
                    _uiMutex.withLock {
                        when (action) {
                            ItemDetailAction.ClearData -> {
                                _uiState.update { ItemDetailUiState() }
                            }

                            is ItemDetailAction.ChangeIdAndVersion -> {
                                val id = action.selectedId
                                val version = action.version
                                changeData(id, version)
                            }
                        }
                    }
                }
            }
        }
    }
}

sealed interface ItemDetailAction {
    data object ClearData : ItemDetailAction

    data class ChangeIdAndVersion(val selectedId: String, val version: String) : ItemDetailAction
}

data class ItemDetailUiState(
    val selectedId: String = "",
    val version: String = "",
    val itemData: ItemData = ItemData(),
    val itemCombination: ItemCombination = ItemCombination(),
)