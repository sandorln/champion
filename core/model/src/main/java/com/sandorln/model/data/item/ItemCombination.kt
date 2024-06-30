package com.sandorln.model.data.item

import com.sandorln.model.data.image.LOLImage

data class ItemCombination(
    var id: String = "",
    var version: String = "",
    val name: String = "",
    val image: LOLImage = LOLImage(),
    val gold: ItemData.Gold = ItemData.Gold(),
    val fromItemList: List<ItemCombination> = emptyList()
)