package com.sandorln.model.data.champion

import com.sandorln.model.data.image.LOLImage

data class ChampionSpell(
        var id: String = "P",
        var name: String = "",
        var description: String = "",
        var image: LOLImage = LOLImage()
    )