package com.sandorln.network.model.champion

import com.sandorln.network.model.NetworkLOLImage
import kotlinx.serialization.Serializable

@Serializable
data class NetworkChampionPassive(
    val name: String = "",
    val description: String = "",
    val image : NetworkLOLImage = NetworkLOLImage()
)