package com.sandorln.item.ui.builder.edit

import com.sandorln.model.data.item.ItemData
import com.sandorln.model.type.ChampionTag

sealed interface ItemBuilderEditAction {
    data class ChangeTitle(val title: String) : ItemBuilderEditAction
    data class TogglePosition(val position: ChampionTag) : ItemBuilderEditAction
    data class ChangeSearchKeyword(val searchKeyword: String) : ItemBuilderEditAction
    data class ChangeSpan(val span: Int) : ItemBuilderEditAction
    data class SelectItemId(val itemDataId: String?) : ItemBuilderEditAction
    data class AddItem(val itemData: ItemData) : ItemBuilderEditAction
    data class RemoveItemByIndex(val index: Int) : ItemBuilderEditAction
    data object Save : ItemBuilderEditAction
}
