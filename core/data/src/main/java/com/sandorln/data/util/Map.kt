package com.sandorln.data.util

import com.sandorln.database.model.ItemEntity
import com.sandorln.model.data.map.MapType

private val MODERN_SUMMONER_RIFT_NAME = listOf("11")
private val CLASSIC_SUMMONER_RIFT_NAME = listOf("1", "2", "SummonersRift")
private val ARAM_NAME = listOf("12", "14")

fun Map<String, Boolean>.asMapTypeEntity(): ItemEntity.MapTypeEntity {
    val modernMapType = this.filterKeys { name -> MODERN_SUMMONER_RIFT_NAME.contains(name) }
    val classicMapType = this.filterKeys { name -> CLASSIC_SUMMONER_RIFT_NAME.contains(name) }
    val aramMapType = this.filterKeys { name -> ARAM_NAME.contains(name) }

    val isModernRift = modernMapType.values.any { it }
    val isAram = aramMapType.values.any { it }
    val isClassicRift = classicMapType.values.any { it }

    return when {
        isModernRift && isAram -> ItemEntity.MapTypeEntity.ALL
        isModernRift && !isAram -> ItemEntity.MapTypeEntity.SUMMONER_RIFT
        !isModernRift && isAram -> ItemEntity.MapTypeEntity.ARAM
        !isModernRift && !isAram && isClassicRift -> ItemEntity.MapTypeEntity.CLASSIC_SUMMONER_RIFT
        else -> ItemEntity.MapTypeEntity.NONE
    }
}

fun ItemEntity.MapTypeEntity.asData(): MapType = when (this) {
    ItemEntity.MapTypeEntity.ALL -> MapType.ALL
    ItemEntity.MapTypeEntity.SUMMONER_RIFT -> MapType.SUMMONER_RIFT
    ItemEntity.MapTypeEntity.ARAM -> MapType.ARAM
    ItemEntity.MapTypeEntity.CLASSIC_SUMMONER_RIFT -> MapType.CLASSIC_SUMMONER_RIFT
    ItemEntity.MapTypeEntity.NONE -> MapType.NONE
}