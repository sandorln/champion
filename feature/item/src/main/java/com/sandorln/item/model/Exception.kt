package com.sandorln.item.model

sealed interface ItemBuildException {
    data object DuplicateLegendaryItem : ItemBuildException
    data object MaxItemSizeReached : ItemBuildException
}