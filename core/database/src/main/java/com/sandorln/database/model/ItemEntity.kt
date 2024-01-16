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
    /* TODO :: [칼바람]과 [소환사 협곡]만 저장 되도록 */
    val maps: MapsEntity = MapsEntity(),
) {
    data class GoldEntity(
        val base: Int = 0,
        val purchasable: Boolean = true,
        val sell: Int = 0,
        val total: Int = 0
    )

    data class MapsEntity(
        val x1: Boolean = false,
        val x10: Boolean = false,
        val x12: Boolean = false,
        val x8: Boolean = false
    )
}