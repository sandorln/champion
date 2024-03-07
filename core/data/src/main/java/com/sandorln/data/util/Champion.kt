package com.sandorln.data.util

import com.sandorln.database.model.ChampionEntity
import com.sandorln.database.model.ChampionTagEntity
import com.sandorln.model.data.champion.ChampionInfo
import com.sandorln.model.data.champion.ChampionStats
import com.sandorln.model.data.champion.SummaryChampion
import com.sandorln.model.type.ChampionTag
import com.sandorln.network.model.champion.NetworkChampion

fun ChampionEntity.asData(): SummaryChampion = SummaryChampion(
    id = id,
    key = key,
    name = name,
    title = title,
    image = image.asData(),
    info = info.asData(),
    tags = tags.asData(),
    partype = partype,
    stats = stats.asData()
)

fun ChampionEntity.ChampionStatsEntity.asData(): ChampionStats = ChampionStats(
    hp = hp,
    hpperlevel = hpperlevel,
    mp = mp,
    mpperlevel = mpperlevel,
    movespeed = movespeed,
    armor = armor,
    armorperlevel = armorperlevel,
    spellblock = spellblock,
    spellblockperlevel = spellblockperlevel,
    attackrange = attackrange,
    hpregen = hpregen,
    hpregenperlevel = hpregenperlevel,
    mpregen = mpregen,
    mpregenperlevel = mpregenperlevel
)

fun ChampionEntity.ChampionInfoEntity.asData(): ChampionInfo = ChampionInfo(
    attack = attack,
    defense = defense,
    magic = magic,
    difficulty = difficulty
)

fun List<ChampionTagEntity>.asData(): List<ChampionTag> = map {
    when (it) {
        ChampionTagEntity.Fighter -> ChampionTag.Fighter
        ChampionTagEntity.Tank -> ChampionTag.Tank
        ChampionTagEntity.Mage -> ChampionTag.Mage
        ChampionTagEntity.Assassin -> ChampionTag.Assassin
        ChampionTagEntity.Ranged, ChampionTagEntity.Marksman -> ChampionTag.Marksman
        ChampionTagEntity.Support -> ChampionTag.Support
    }
}

fun NetworkChampion.asEntity(version: String): ChampionEntity = ChampionEntity(
    version = version,
    id = id,
    key = key,
    name = name,
    title = title,
    tags = tags.asChampionTagEntity(),
    partype = partype,
    stats = stats.asEntity(),
    info = info.asEntity(),
    image = image.asEntity(),
)

fun NetworkChampion.NetworkChampionInfo.asEntity(): ChampionEntity.ChampionInfoEntity = ChampionEntity.ChampionInfoEntity(
    attack = attack,
    defense = defense,
    magic = magic,
    difficulty = difficulty
)

fun NetworkChampion.NetworkChampionStats.asEntity(): ChampionEntity.ChampionStatsEntity = ChampionEntity.ChampionStatsEntity(
    hp = hp,
    hpperlevel = hpperlevel,
    mp = mp,
    mpperlevel = mpperlevel,
    movespeed = movespeed,
    armor = armor,
    armorperlevel = armorperlevel,
    spellblock = spellblock,
    spellblockperlevel = spellblockperlevel,
    attackrange = attackrange,
    hpregen = hpregen,
    hpregenperlevel = hpregenperlevel,
    mpregen = mpregen
)

fun List<String>.asChampionTagEntity(): List<ChampionTagEntity> = mapNotNull {
    runCatching { ChampionTagEntity.valueOf(it) }.getOrNull()
}