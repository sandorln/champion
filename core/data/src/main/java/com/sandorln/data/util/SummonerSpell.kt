package com.sandorln.data.util

import com.sandorln.database.model.SummonerSpellEntity
import com.sandorln.model.data.spell.SummonerSpell
import com.sandorln.network.model.NetworkSummonerSpell

fun SummonerSpellEntity.asData(): SummonerSpell = SummonerSpell(
    id = id,
    version = version,
    name = name,
    description = description,
    cooldownBurn = cooldownBurn,
    image = image.asData()
)

fun NetworkSummonerSpell.asEntity(version: String): SummonerSpellEntity = SummonerSpellEntity(
    id = id,
    version = version,
    name = name,
    description = description,
    cooldownBurn = cooldownBurn,
    image = image.asEntity()
)