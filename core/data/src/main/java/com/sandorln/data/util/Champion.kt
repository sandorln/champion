package com.sandorln.data.util

import com.sandorln.database.model.ChampionEntity
import com.sandorln.database.model.ChampionTagEntity
import com.sandorln.model.data.champion.ChampionDetailData
import com.sandorln.model.data.champion.ChampionInfo
import com.sandorln.model.data.champion.ChampionPatchNote
import com.sandorln.model.data.champion.ChampionSkin
import com.sandorln.model.data.champion.ChampionSpell
import com.sandorln.model.data.champion.ChampionStats
import com.sandorln.model.data.champion.SummaryChampion
import com.sandorln.model.type.ChampionTag
import com.sandorln.model.type.SpellType
import com.sandorln.network.model.champion.NetworkChampion
import com.sandorln.network.model.champion.NetworkChampionDetail
import com.sandorln.network.model.champion.NetworkChampionPassive
import com.sandorln.network.model.champion.NetworkChampionPatchNote
import com.sandorln.network.model.champion.NetworkChampionSkin
import com.sandorln.network.model.champion.NetworkChampionSpell

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
    attackdamage = attackdamage,
    attackdamageperlevel = attackdamageperlevel,
    attackspeedoffset = attackspeedoffset,
    attackspeed = attackspeed,
    attackspeedperlevel = attackspeedperlevel,
    attackrange = attackrange,
    crit = crit,
    critperlevel = critperlevel,
    hpregen = hpregen,
    hpregenperlevel = hpregenperlevel,
    mpregen = mpregen,
    mpregenperlevel = mpregenperlevel,
    spellblock = spellblock,
    spellblockperlevel = spellblockperlevel,
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
        ChampionTagEntity.Marksman -> ChampionTag.Marksman
        ChampionTagEntity.Support -> ChampionTag.Support
    }
}

fun List<ChampionTag>.asEntity(): List<ChampionTagEntity> = map {
    when (it) {
        ChampionTag.Fighter -> ChampionTagEntity.Fighter
        ChampionTag.Tank -> ChampionTagEntity.Tank
        ChampionTag.Mage -> ChampionTagEntity.Mage
        ChampionTag.Assassin -> ChampionTagEntity.Assassin
        ChampionTag.Marksman -> ChampionTagEntity.Marksman
        ChampionTag.Support -> ChampionTagEntity.Support
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
    attackdamage = attackdamage,
    attackdamageperlevel = attackdamageperlevel,
    attackspeedoffset = attackspeedoffset,
    attackspeed = attackspeed,
    attackspeedperlevel = attackspeedperlevel,
    attackrange = attackrange,
    crit = crit,
    critperlevel = critperlevel,
    hpregen = hpregen,
    hpregenperlevel = hpregenperlevel,
    mpregen = mpregen,
    mpregenperlevel = mpregenperlevel,
    spellblock = spellblock,
    spellblockperlevel = spellblockperlevel
)

fun List<String>.asChampionTagEntity(): List<ChampionTagEntity> = this
    .sorted()
    .mapNotNull { tag ->
        runCatching {
            when {
                tag == "Ranged" -> ChampionTagEntity.Marksman
                else -> ChampionTagEntity.valueOf(tag)
            }
        }.getOrNull()
    }

fun ChampionEntity.asDetailData(): ChampionDetailData = ChampionDetailData(
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

fun NetworkChampionDetail.asData(otherChampionDetail: ChampionDetailData = ChampionDetailData()): ChampionDetailData =
    otherChampionDetail.copy(
        lore = lore,
        skins = skins.map(NetworkChampionSkin::asData),
        allytips = allyTips,
        enemytips = enemyTips,
        spells = spells.mapIndexed { index, networkChampionSpell ->
            networkChampionSpell.asData(index + 1)
        },
        passive = passive.asData()
    )

fun NetworkChampionSkin.asData(): ChampionSkin = ChampionSkin(
    id = id,
    name = name,
    num = num,
    chromas = chromas
)

fun NetworkChampionSpell.asData(index: Int): ChampionSpell {
    val spellType = runCatching {
        SpellType.entries[index]
    }.fold(
        onSuccess = { it },
        onFailure = { SpellType.Q }
    )

    return ChampionSpell(
        spellType = spellType,
        name = name,
        description = description,
        image = image.asData(),
        tooltip = tooltip,
        cooldownBurn = cooldownBurn,
        costBurn = costBurn,
        levelTip = levelTip.label
    )
}

fun NetworkChampionPassive.asData(): ChampionSpell = ChampionSpell(
    spellType = SpellType.P,
    name = name,
    description = description,
    image = image.asData()
)

fun NetworkChampionPatchNote.asData(): ChampionPatchNote = ChampionPatchNote(
    title = title,
    image = image,
    summary = summary,
    detailPathStory = detailPathStory
)