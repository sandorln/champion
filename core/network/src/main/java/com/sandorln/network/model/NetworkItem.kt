package com.sandorln.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkItem(
    val name: String = "",
    val description: String = "",

    val depth: Int = 0,
    val inStore: Boolean = true,

    val from: List<String> = mutableListOf(),
    val into: List<String> = mutableListOf(),

    val gold: NetworkGold = NetworkGold(),

    val tags: List<String> = mutableListOf(),
){
    @Serializable
    data class NetworkGold(
        val base: Int = 0,
        val purchasable: Boolean = true,
        val sell: Int = 0,
        val total: Int = 0
    )
}