package com.sandorln.model.data.version

data class Version(
    val name: String = "",

    val isCompleteChampions: Boolean = false,
    val isDownLoadChampionIconSprite: Boolean = false,

    val isCompleteItems: Boolean = false,
    val isDownLoadItemIconSprite: Boolean = false,

    val isCompleteSummonerSpell: Boolean = false,
    val isDownLoadSpellIconSprite: Boolean = false,
    
    // TODO :: Rune 정보 추가 필요

    val newItemIdList: List<String>? = null,
    val newChampionIdList: List<String>? = null
)