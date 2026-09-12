package com.sandorln.model.data.item

import com.sandorln.model.type.ChampionTag

data class ItemBuild(
    val id: Long = 0L,
    val version: String = "",
    val title: String = "",
    val positionList: List<ChampionTag> = emptyList(),
    val itemIdList: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)
