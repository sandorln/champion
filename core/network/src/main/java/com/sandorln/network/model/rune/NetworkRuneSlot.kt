package com.sandorln.network.model.rune

import kotlinx.serialization.Serializable

@Serializable
data class NetworkRuneSlot(
    val runes: List<NetworkRuneData>
)