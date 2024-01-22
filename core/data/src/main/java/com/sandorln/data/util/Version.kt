package com.sandorln.data.util

import com.sandorln.database.model.VersionEntity
import com.sandorln.model.data.version.Version

fun VersionEntity.asData(): Version = Version(
    name = name,
    isCompleteChampions = isCompleteChampions,
    isDownLoadChampionIconSprite = isDownLoadChampionIconSprite,
    isCompleteItems = isCompleteItems,
    isDownLoadItemIconSprite = isDownLoadItemIconSprite,
    isCompleteSummonerSpell = isCompleteSummonerSpell,
    isDownLoadSpellIconSprite = isDownLoadSpellIconSprite
)

fun Version.asEntity(): VersionEntity = VersionEntity(
    name = name,
    isCompleteChampions = isCompleteChampions,
    isDownLoadChampionIconSprite = isDownLoadChampionIconSprite,
    isCompleteItems = isCompleteItems,
    isDownLoadItemIconSprite = isDownLoadItemIconSprite,
    isCompleteSummonerSpell = isCompleteSummonerSpell,
    isDownLoadSpellIconSprite = isDownLoadSpellIconSprite
)