package com.sandorln.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item_build")
data class ItemBuildEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val version: String = "",
    val title: String = "",
    val positions: List<ChampionTagEntity> = emptyList(),
    val itemIds: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)
