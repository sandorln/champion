package com.sandorln.data.util

import com.sandorln.database.model.ChampionEntity
import com.sandorln.model.data.champion.ChampionInfo
import com.sandorln.model.data.champion.ChampionStats
import com.sandorln.model.data.champion.SummaryChampion
import com.sandorln.network.model.NetworkChampion

fun ChampionEntity.asData(): SummaryChampion = SummaryChampion(
    id = id,
    key = key,
    name = name,
    title = title,
    blurb = blurb,
    image = image.asData(),
    info = info.asData(),
    tags = tags,
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

fun NetworkChampion.asEntity(version: String): ChampionEntity = ChampionEntity(
    version = version,
    id = id,
    key = key,
    name = name,
    title = title,
    blurb = blurb,
    tags = tags,
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