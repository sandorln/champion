package com.sandorln.database.model

import androidx.room.Entity
import com.sandorln.database.model.base.LOLImageEntity

@Entity(primaryKeys = ["id", "version"])
data class SummonerSpellEntity(
    val id: String = "",
    var version: String = "",
    val name: String = "",
    val description: String = "",
    val cooldownBurn: String = "",

    var image: LOLImageEntity = LOLImageEntity()
)