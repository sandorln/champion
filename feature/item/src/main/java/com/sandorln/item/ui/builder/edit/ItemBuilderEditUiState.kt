package com.sandorln.item.ui.builder.edit

import android.graphics.Bitmap
import com.sandorln.model.data.item.ItemData
import com.sandorln.model.type.ChampionTag

data class ItemBuilderEditUiState(
    val id: Long = 0L,
    val currentVersionName: String = "",
    val title: String = "",
    val selectedPositions: Set<ChampionTag> = emptySet(),
    val selectedItems: List<ItemData> = emptyList(),
    val searchKeyword: String = "",
    val selectedItemId: String? = null,
    val currentSpriteMap: Map<String, Bitmap?> = emptyMap(),
    val bootItemList: List<List<ItemData>> = emptyList(),
    val consumableItemList: List<List<ItemData>> = emptyList(),
    val normalItemList: List<List<ItemData>> = emptyList(),
    val epicItemList: List<List<ItemData>> = emptyList(),
    val legendItemList: List<List<ItemData>> = emptyList(),
    val orrnItemList: List<List<ItemData>> = emptyList()
) {
    val canSave: Boolean
        get() = title.isNotBlank() && selectedPositions.isNotEmpty() && selectedItems.isNotEmpty()
}
