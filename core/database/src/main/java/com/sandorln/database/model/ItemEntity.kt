package com.sandorln.database.model

import androidx.room.Entity
import com.sandorln.database.model.base.LOLImageEntity

@Entity(primaryKeys = ["id", "version"])
data class ItemEntity(
    var id: String = "",
    var version: String = "",
    val name: String = "",
    val description: String = "",

    val depth: Int = 0,
    val inStore: Boolean = true,

    val from: List<String> = mutableListOf(),
    val into: List<String> = mutableListOf(),

    var image: LOLImageEntity = LOLImageEntity(),

    val gold: GoldEntity = GoldEntity(),
    val tags: List<String> = mutableListOf(),

    val maps: MapTypeEntity = MapTypeEntity.NONE,
) {
    data class GoldEntity(
        val base: Int = 0,
        val purchasable: Boolean = true,
        val sell: Int = 0,
        val total: Int = 0
    )

    enum class MapTypeEntity {
        ALL,
        SUMMONER_RIFT,
        ARAM,
        NONE
    }
}