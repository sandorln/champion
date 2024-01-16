package com.sandorln.database.model

import androidx.room.Entity

@Entity(primaryKeys = ["version"])
data class VersionEntity(
    val version: String = "",

    val isCompleteChampions: Boolean = false,
    val isDownLoadChampionIconSprite: Boolean = false,

    val isCompleteItems: Boolean = false,
    val isDownLoadItemIconSprite: Boolean = false,

    val isCompleteSummonerSpell: Boolean = false,
    val isDownLoadSpellIconSprite: Boolean = false
)