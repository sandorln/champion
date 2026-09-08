package com.sandorln.item.model

sealed class ItemBuildException : Exception() {
    class DuplicateLegendaryItem : ItemBuildException()
    class MaxItemSizeReached : ItemBuildException()
}