package com.sandorln.network.model.rune

import kotlinx.serialization.Serializable

@Serializable
data class NetworkRuneData(
    val id: Int,
    val key: String,
    val icon: String,
    val name: String,
    val shortDesc: String,
    val longDesc: String
)
