package com.sandorln.item.ui.builder.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.domain.usecase.item.GetItemListByCurrentVersion
import com.sandorln.domain.usecase.itembuild.DeleteItemBuild
import com.sandorln.domain.usecase.itembuild.GetItemBuildListByVersion
import com.sandorln.domain.usecase.sprite.GetCurrentVersionDistinctBySpriteType
import com.sandorln.domain.usecase.sprite.GetSpriteBitmapByCurrentVersion
import com.sandorln.domain.usecase.version.GetCurrentVersion
import com.sandorln.model.data.image.SpriteType
import com.sandorln.model.data.item.ItemBuild
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ItemBuilderListViewModel @Inject constructor(
    getCurrentVersion: GetCurrentVersion,
    getItemBuildListByVersion: GetItemBuildListByVersion,
    private val deleteItemBuild: DeleteItemBuild,
    getItemListByCurrentVersion: GetItemListByCurrentVersion,
    getSpriteBitmapByCurrentVersion: GetSpriteBitmapByCurrentVersion,
    getCurrentVersionDistinctBySpriteType: GetCurrentVersionDistinctBySpriteType
) : ViewModel() {

    private val _uiState = MutableStateFlow(ItemBuilderListUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<ItemBuilderListSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    init {
        // Collect current version and its builds
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

        // Combine item builds and item list
        viewModelScope.launch {
            _uiState
                .map { it.currentVersionName }
                .distinctUntilChanged()
                .filterNotNull()
                .flatMapLatest { version ->
                    combine(
                        getItemBuildListByVersion(version),
                        getItemListByCurrentVersion()
                    ) { buildList, itemList ->
                        val itemMap = itemList.associateBy { it.id }
                        buildList.map { build ->
                            ItemBuildUiModel(
                                itemBuild = build,
                                itemList = build.itemIdList.mapNotNull { itemMap[it] }
                            )
                        }
                    }
                }
                .collectLatest { uiModels ->
                    _uiState.update {
                        it.copy(
                            buildList = uiModels,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun sendAction(action: ItemBuilderListAction) {
        when (action) {
            is ItemBuilderListAction.ShowDeleteDialog -> {
                _uiState.update { it.copy(deleteTargetBuild = action.itemBuild) }
            }
            is ItemBuilderListAction.DeleteItemBuild -> {
                viewModelScope.launch {
                    try {
                        deleteItemBuild(action.id)
                        _uiState.update { it.copy(deleteTargetBuild = null) }
                        _sideEffect.emit(ItemBuilderListSideEffect.BuildDeleted)
                    } catch (e: Exception) {
                        _sideEffect.emit(ItemBuilderListSideEffect.ShowToast(e.message ?: "삭제 실패"))
                    }
                }
            }
        }
    }
}
