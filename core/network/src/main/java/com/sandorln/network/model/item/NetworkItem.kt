package com.sandorln.network.model.item

import com.sandorln.network.model.NetworkLOLImage
import kotlinx.serialization.Serializable

@Serializable
data class NetworkItem(
    val name: String = "",
    val description: String = "",
    val image: NetworkLOLImage = NetworkLOLImage(),

    val depth: Int = 0,
    val inStore: Boolean = true,

    val from: List<String?> = mutableListOf(),
    val into: List<String?> = mutableListOf(),

    val gold: NetworkGold = NetworkGold(),

    val tags: List<String> = mutableListOf(),
    val maps: Map<String, Boolean> = mapOf()
) {
    @Serializable
    data class NetworkGold(
        val base: Int = 0,
        val purchasable: Boolean = true,
        val sell: Int = 0,
        val total: Int = 0
    )
}