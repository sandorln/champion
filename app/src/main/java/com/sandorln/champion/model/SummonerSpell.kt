package com.sandorln.champion.model

import androidx.room.Entity

@Entity(primaryKeys = ["id", "version"])
data class SummonerSpell(
    val id: String = "",
    var version: String = "",
    val name: String = "",
    val description: String = "",
    val cooldownBurn: String = ""
)