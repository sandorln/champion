package com.sandorln.data.util

import com.sandorln.database.model.ItemEntity
import com.sandorln.model.data.map.MapType

private val SUMMONER_RIFT_NAME = listOf(
    "1",
    "2",
    "11",
    /* Old Version */
    "SummonersRift"
)

private val ARAM_NAME = listOf(
    "12",
    /* Old Version */
    "14"
)

fun Map<String, Boolean>.asMapTypeEntity(): ItemEntity.MapTypeEntity {
    val isNotSummonerRift = this.filterKeys { name -> SUMMONER_RIFT_NAME.contains(name) }.values.any { !it }
    val isNotAram = this.filterKeys { name -> ARAM_NAME.contains(name) }.values.any { !it }

    return when {
        !isNotSummonerRift && !isNotAram -> ItemEntity.MapTypeEntity.ALL
        isNotSummonerRift && !isNotAram -> ItemEntity.MapTypeEntity.ARAM
        !isNotSummonerRift && isNotAram -> ItemEntity.MapTypeEntity.SUMMONER_RIFT
        else -> ItemEntity.MapTypeEntity.NONE
    }
}

fun ItemEntity.MapTypeEntity.asData(): MapType = when (this) {
    ItemEntity.MapTypeEntity.ALL -> MapType.ALL
    ItemEntity.MapTypeEntity.SUMMONER_RIFT -> MapType.SUMMONER_RIFT
    ItemEntity.MapTypeEntity.ARAM -> MapType.ARAM
    ItemEntity.MapTypeEntity.NONE -> MapType.NONE
}