package com.sandorln.model.data.champion

import com.sandorln.model.data.image.LOLImage

data class ChampionSpell(
    var id: String = "P",
    var name: String = "",

    val tooltip: String = "",
    var description: String = "",

    val cooldownBurn: String = "",
    val costBurn: String = "",

    val levelTip: List<String> = listOf(),

    var image: LOLImage = LOLImage()
)