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

private val ARENA_NAME = listOf(
    "30"
)

fun Map<String, Boolean>.asMapTypeEntity(): ItemEntity.MapTypeEntity {
    val summonerMapType = this.filterKeys { name -> SUMMONER_RIFT_NAME.contains(name) }
    val aramMapType = this.filterKeys { name -> ARAM_NAME.contains(name) }
    val arenaMapType = this.filterKeys { name -> ARENA_NAME.contains(name) }

    val isSummonerRift = if (summonerMapType.isNotEmpty()) {
        summonerMapType.values.any { it }
    } else {
        true
    }

    val isAram = if (aramMapType.isNotEmpty()) {
        aramMapType.values.any { it }
    } else {
        true
    }

    val isArena = if (arenaMapType.isNotEmpty()) {
        arenaMapType.values.any { it }
    } else {
        false
    }

    return when {
        isArena && !isSummonerRift && !isAram -> ItemEntity.MapTypeEntity.ARENA
        isSummonerRift && isAram -> ItemEntity.MapTypeEntity.ALL
        !isSummonerRift && isAram -> ItemEntity.MapTypeEntity.ARAM
        isSummonerRift && !isAram -> ItemEntity.MapTypeEntity.SUMMONER_RIFT
        isArena -> ItemEntity.MapTypeEntity.ARENA
        else -> ItemEntity.MapTypeEntity.NONE
    }
}

fun ItemEntity.MapTypeEntity.asData(): MapType = when (this) {
    ItemEntity.MapTypeEntity.ALL -> MapType.ALL
    ItemEntity.MapTypeEntity.SUMMONER_RIFT -> MapType.SUMMONER_RIFT
    ItemEntity.MapTypeEntity.ARAM -> MapType.ARAM
    ItemEntity.MapTypeEntity.NONE -> MapType.NONE
    ItemEntity.MapTypeEntity.CLASSIC -> MapType.CLASSIC
    ItemEntity.MapTypeEntity.ARENA -> MapType.ARENA
}