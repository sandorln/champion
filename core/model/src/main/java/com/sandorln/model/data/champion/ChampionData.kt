package com.sandorln.model.data.champion

import com.sandorln.model.data.image.LOLImage
import com.sandorln.model.type.ChampionTag

data class ChampionData(
    var version: String = "",
    var id: String = "",
    var key: Int = 0,
    var name: String = "",
    var title: String = "",
    var blurb: String = "",
    var info: ChampionInfo = ChampionInfo(),
    var image: LOLImage = LOLImage(),
    var tags: List<ChampionTag> = mutableListOf(),
    var partype: String = "",
    var stats: ChampionStats = ChampionStats(),
    var skins: List<ChampionSkin> = mutableListOf(),

    var spells: List<ChampionSpell> = mutableListOf(),
    var passive: ChampionSpell = ChampionSpell(),

    var allytips: List<String> = mutableListOf(),
    var enemytips: List<String> = mutableListOf()
)