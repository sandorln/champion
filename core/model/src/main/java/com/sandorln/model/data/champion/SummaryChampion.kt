package com.sandorln.model.data.champion

import com.sandorln.model.data.image.LOLImage

data class SummaryChampion(
    var id: String = "",
    var key: Int = 0,
    var name: String = "",
    var title: String = "",
    var tags: List<String> = mutableListOf(),
    var partype: String = "",

    var info: ChampionInfo = ChampionInfo(),
    var image: LOLImage = LOLImage(),
    var stats: ChampionStats = ChampionStats()
)