package com.sandorln.champion.model

import androidx.room.Entity

@Entity(primaryKeys = ["id", "version", "languageCode"])
data class SummonerSpell(
    val id: String = "",
    var version: String = "",
    var languageCode: String = "",
    val name: String = "",
    val description: String = "",
    val cooldownBurn: String = ""
)