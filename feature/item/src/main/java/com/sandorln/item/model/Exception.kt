package com.sandorln.item.model

sealed interface ItemBuildException {
    data object NotAddSameLegendItem : Exception(), ItemBuildException
    data object NotShouldAddItemSize : Exception(), ItemBuildException
}