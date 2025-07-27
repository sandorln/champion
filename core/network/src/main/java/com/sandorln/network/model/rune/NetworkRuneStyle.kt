package com.sandorln.network.model.rune

import kotlinx.serialization.Serializable

@Serializable
data class NetworkRuneStyle(
    val id: Int,
    val key: String,
    val icon: String,
    val name: String,
    val slots: List<NetworkRuneSlot>
)