package com.sandorln.data.util

import com.sandorln.database.model.ItemEntity
import com.sandorln.model.data.map.MapType

private val MODERN_SUMMONER_RIFT_NAME = listOf("11")
private val CLASSIC_SUMMONER_RIFT_NAME = listOf("1", "2", "SummonersRift")
private val ARAM_NAME = listOf("12", "14")

fun Map<String, Boolean>.asMapTypeEntity(itemId: String = ""): ItemEntity.MapTypeEntity {
    // 1. 6자리 이상의 모드/변형 아이템(32xxxx, 77xxxx, 22xxxx 등)은 모드 전용(NONE)으로 격리
    if (itemId.length > 4) {
        return ItemEntity.MapTypeEntity.NONE
    }

    // 2. 14+ 시즌 이후 정규 모드 플래그 '453'이 명시적으로 false인 변형 아이템 제외
    if (this.containsKey("453") && this["453"] == false) {
        return ItemEntity.MapTypeEntity.NONE
    }

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