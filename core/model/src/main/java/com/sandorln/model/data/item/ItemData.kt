package com.sandorln.model.data.item


data class ItemData(
    var id: String = "",
    var version: String = "",
    var languageCode: String = "",
    val name: String = "",
    val description: String = "",

    val depth: Int = 0,
    val inStore: Boolean = true,

    val from: List<String> = mutableListOf(),
    val into: List<String> = mutableListOf(),

    val gold: Gold = Gold(),

    val tags: List<String> = mutableListOf(),
) {
    data class Gold(
        val base: Int = 0,
        val purchasable: Boolean = true,
        val sell: Int = 0,
        val total: Int = 0
    )
}