package com.sandorln.database.model

import androidx.room.Entity

@Entity(primaryKeys = ["name"])
data class VersionEntity(
    val name: String = "",

    val isCompleteChampions: Boolean = false,
    val isDownLoadChampionIconSprite: Boolean = false,

    val isCompleteItems: Boolean = false,
    val isDownLoadItemIconSprite: Boolean = false,

    val isCompleteSummonerSpell: Boolean = false,
    val isDownLoadSpellIconSprite: Boolean = false,

    val newItemIdList : List<String>? = null,
    val newChampionIdList : List<String>? = null
) {
    val isInitCompleteVersion
        get() = isCompleteChampions && isCompleteItems && isCompleteSummonerSpell
}