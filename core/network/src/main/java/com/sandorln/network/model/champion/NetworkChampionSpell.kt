package com.sandorln.network.model.champion

import com.sandorln.network.model.NetworkLOLImage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkChampionSpell(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val tooltip: String = "",
    @SerialName("leveltip")
    val levelTip: NetworkSpellLvTip = NetworkSpellLvTip(),
    val cooldownBurn: String = "",
    val costBurn: String = "",
    val image: NetworkLOLImage = NetworkLOLImage()
) {
    @Serializable
    data class NetworkSpellLvTip(
        val label: List<String> = listOf()
    )
}