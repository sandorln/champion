package com.sandorln.network.model.rune

data class NetworkRuneStyle(
    val id: Int,
    val key: String,
    val icon: String,
    val name: String,
    val slots: List<NetworkRuneSlot>
)