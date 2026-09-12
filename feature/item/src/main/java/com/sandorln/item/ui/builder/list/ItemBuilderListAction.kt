package com.sandorln.item.ui.builder.list

import com.sandorln.model.data.item.ItemBuild

sealed interface ItemBuilderListAction {
    data class ShowDeleteDialog(val itemBuild: ItemBuild?) : ItemBuilderListAction
    data class DeleteItemBuild(val id: Long) : ItemBuilderListAction
}
