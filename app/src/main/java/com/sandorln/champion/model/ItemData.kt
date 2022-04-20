package com.sandorln.champion.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class ItemData(
    @PrimaryKey
    val id: String,
    val colloq: String,
    val consumeOnFull: Boolean,
    val consumed: Boolean,
    val depth: Int,
    val description: String,
    val from: List<String>,
    val gold: Gold,
    val group: String,
    val hideFromAll: Boolean,
    val inStore: Boolean,
    val into: List<String>,
    val maps: Maps,
    val name: String,
    val plaintext: String,
    val requiredAlly: String,
    val requiredChampion: String,
    val specialRecipe: Int,
    val stacks: Int,
    val tags: List<String>
) {
    data class Gold(
        val base: Int,
        val purchasable: Boolean,
        val sell: Int,
        val total: Int
    )

    data class Maps(
        @SerializedName("1")
        val x1: Boolean,
        @SerializedName("10")
        val x10: Boolean,
        @SerializedName("12")
        val x12: Boolean,
        @SerializedName("8")
        val x8: Boolean
    )
}