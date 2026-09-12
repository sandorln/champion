package com.sandorln.item.ui.builder.list

import android.graphics.Bitmap
import com.sandorln.model.data.item.ItemBuild
import com.sandorln.model.data.item.ItemData

data class ItemBuildUiModel(
    val itemBuild: ItemBuild = ItemBuild(),
    val itemList: List<ItemData> = emptyList()
)

data class ItemBuilderListUiState(
    val currentVersionName: String = "",
    val buildList: List<ItemBuildUiModel> = emptyList(),
    val currentSpriteMap: Map<String, Bitmap?> = emptyMap(),
    val deleteTargetBuild: ItemBuild? = null,
    val isLoading: Boolean = false
) {
    val countText: String
        get() = "(${buildList.size}/10)"

    val isMaxBuildCountReached: Boolean
        get() = buildList.size >= 10
}
