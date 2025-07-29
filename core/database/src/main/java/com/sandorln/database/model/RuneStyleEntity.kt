package com.sandorln.database.model

import androidx.room.Entity

@Entity(primaryKeys = ["id", "version"])
data class RuneStyleEntity(
    val id: Int,
    val version: String,
    val key: String,
    val icon: String,
    val name: String,
    val slots: List<RuneSlotEntity>
)

data class RuneSlotEntity(
    val runes: List<RuneDataEntity>
)

data class RuneDataEntity(
    val id: Int,
    val key: String,
    val icon: String,
    val name: String,
    val shortDesc: String,
    val longDesc: String
)