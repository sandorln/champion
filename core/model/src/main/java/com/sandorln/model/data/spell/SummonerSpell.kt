package com.sandorln.model.data.spell

data class SummonerSpell(
    val id: String = "",
    var version: String = "",
    var languageCode: String = "",
    val name: String = "",
    val description: String = "",
    val cooldownBurn: String = ""
)