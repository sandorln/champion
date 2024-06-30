package com.sandorln.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkSummonerSpell(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val cooldownBurn: String = "",

    var image: NetworkLOLImage = NetworkLOLImage()
)