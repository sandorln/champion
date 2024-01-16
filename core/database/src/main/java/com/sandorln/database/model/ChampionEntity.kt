package com.sandorln.database.model

import androidx.room.Entity
import com.sandorln.database.model.base.LOLImageEntity

@Entity(primaryKeys = ["version", "id"])
data class ChampionEntity(
    var version: String = "",
    var id: String = "",
    var key: Int = 0,
    var name: String = "",
    var title: String = "",
    var blurb: String = "",
    var info: ChampionInfoEntity = ChampionInfoEntity(),
    var image: LOLImageEntity = LOLImageEntity(),
    var tags: List<String> = mutableListOf(),
    var partype: String = "",
    var stats: ChampionStatsEntity = ChampionStatsEntity()
) {
    data class ChampionInfoEntity(
        var attack: Int = 0,
        var defense: Int = 0,
        var magic: Int = 0,
        var difficulty: Int = 0
    )

    data class ChampionStatsEntity(
        var hp: Double = 0.0,
        var hpperlevel: Double = 0.0,
        var mp: Double = 0.0,
        var mpperlevel: Double = 0.0,
        var movespeed: Double = 0.0,
        var armor: Double = 0.0,
        var armorperlevel: Double = 0.0,
        var spellblock: Double = 0.0,
        var spellblockperlevel: Double = 0.0,
        var attackrange: Double = 0.0,
        var hpregen: Double = 0.0,
        var hpregenperlevel: Double = 0.0,
        var mpregen: Double = 0.0,
        var mpregenperlevel: Double = 0.0,
        var crit: Double = 0.0,
        var critperlevel: Double = 0.0,
        var attackdamage: Double = 0.0,
        var attackdamageperlevel: Double = 0.0,
        var attackspeed: Double = 0.0,
        var attackspeedoffset: Double = 0.0,
        var attackspeedperlevel: Double = 0.0
    )
}