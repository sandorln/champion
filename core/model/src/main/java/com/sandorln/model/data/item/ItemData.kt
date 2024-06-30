package com.sandorln.model.data.item

import com.sandorln.model.data.image.LOLImage
import com.sandorln.model.data.map.MapType
import com.sandorln.model.type.ItemTagType

data class ItemData(
    var id: String = "",
    var version: String = "",
    val name: String = "",
    val description: String = "",
    val image: LOLImage = LOLImage(),

    val depth: Int = 0,
    val inStore: Boolean = true,

    val from: List<String> = mutableListOf(),
    val into: List<String> = mutableListOf(),

    val gold: Gold = Gold(),

    val tags: Set<ItemTagType> = emptySet(),
    val mapType: MapType = MapType.ALL
) {
    data class Gold(
        val base: Int = 0,
        val purchasable: Boolean = true,
        val sell: Int = 0,
        val total: Int = 0
    )
}