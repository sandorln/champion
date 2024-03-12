package com.sandorln.network.model.champion

import kotlinx.serialization.Serializable

@Serializable
data class NetworkChampionSkin(
    val id: String = "",
    val name: String = "",
    val num: String = "",
    var chromas: Boolean = false
)