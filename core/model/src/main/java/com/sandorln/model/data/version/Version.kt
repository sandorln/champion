package com.sandorln.model.data.version

data class Version(
    val name: String = "",

    val isCompleteChampions: Boolean = false,
    val isDownLoadChampionIconSprite: Boolean = false,

    val isCompleteItems: Boolean = false,
    val isDownLoadItemIconSprite: Boolean = false,

    val isCompleteSummonerSpell: Boolean = false,
    val isDownLoadSpellIconSprite: Boolean = false,

    val isCompleteRune: Boolean = false,

    val newItemIdList: List<String>? = null,
    val newChampionIdList: List<String>? = null
) {
    val majorMinorPatch: List<Int> = runCatching { name.split('.').map(String::toInt) }.getOrDefault(emptyList())
    val isInitCompleteVersion: Boolean
        get() = isCompleteChampions && isCompleteItems && isCompleteSummonerSpell && isCompleteRune
}