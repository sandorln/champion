package com.sandorln.network.model.champion

import kotlinx.serialization.Serializable

@Serializable
data class NetworkChampionSkin(
    val id: String = "",
    val name: String = "",
    val num: Int = 0,
    var chromas: Boolean = false,
    val parentSkin: Int? = null
)