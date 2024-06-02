package com.sandorln.network.model.champion

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkChampionDetail(
    val lore: String = "",
    @SerialName("allytips")
    val allyTips: List<String> = listOf(),
    @SerialName("enemytips")
    val enemyTips: List<String> = listOf(),

    val skins: List<NetworkChampionSkin> = listOf(),
    val spells: List<NetworkChampionSpell> = listOf(),
    val passive: NetworkChampionPassive = NetworkChampionPassive(),
    val rating: Float = 0f,
    val writingRating : Int = 0
)