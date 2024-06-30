package com.sandorln.model.data.champion

import com.sandorln.model.data.image.LOLImage
import com.sandorln.model.type.SpellType

data class ChampionSpell(
    var spellType: SpellType = SpellType.P,
    var name: String = "",

    val tooltip: String = "",
    var description: String = "",

    val cooldownBurn: String = "",
    val costBurn: String = "",

    val levelTip: List<String> = listOf(),

    var image: LOLImage = LOLImage()
)