package com.sandorln.champion.model


import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(primaryKeys = ["id", "version"])
data class ItemData(
    var id: String = "",
    var version: String = "",
    val name: String = "",
    val colloq: String = "",
    val description: String = "",

    val depth: Int = 0,
    val inStore: Boolean = true,

    val from: List<String> = mutableListOf(),
    val into: List<String> = mutableListOf(),

    val gold: Gold = Gold(),

    val tags: List<String> = mutableListOf(),
    val maps: Maps = Maps(),
) {
    data class Gold(
        val base: Int = 0,
        val purchasable: Boolean = true,
        val sell: Int = 0,
        val total: Int = 0
    )

    data class Maps(
        @SerializedName("1")
        val x1: Boolean = false,
        @SerializedName("10")
        val x10: Boolean = false,
        @SerializedName("12")
        val x12: Boolean = false,
        @SerializedName("8")
        val x8: Boolean = false
    )
}