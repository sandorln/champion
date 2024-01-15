package com.sandorln.database.model

import androidx.room.Entity

@Entity(primaryKeys = ["version"])
data class CompleteVersionEntity(
    val version: String = "",
    val isCompleteChampions: Boolean = false,
    val isCompleteItems: Boolean = false,
    val isCompleteSummonerSpell: Boolean = false
)