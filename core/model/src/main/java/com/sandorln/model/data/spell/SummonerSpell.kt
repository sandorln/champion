package com.sandorln.model.data.spell

import com.sandorln.model.data.image.LOLImage

data class SummonerSpell(
    val id: String = "",
    var version: String = "",
    val name: String = "",
    val description: String = "",
    val cooldownBurn: String = "",
    val image: LOLImage = LOLImage()
)