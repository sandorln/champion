package com.sandorln.champion.model


import com.google.gson.annotations.SerializedName

data class ItemData(
    @SerializedName("colloq")
    val colloq: String,
    @SerializedName("consumeOnFull")
    val consumeOnFull: Boolean,
    @SerializedName("consumed")
    val consumed: Boolean,
    @SerializedName("depth")
    val depth: Int,
    @SerializedName("description")
    val description: String,
    @SerializedName("from")
    val from: List<String>,
    @SerializedName("gold")
    val gold: Gold,
    @SerializedName("group")
    val group: String,
    @SerializedName("hideFromAll")
    val hideFromAll: Boolean,
    @SerializedName("inStore")
    val inStore: Boolean,
    @SerializedName("into")
    val into: List<Any>,
    @SerializedName("maps")
    val maps: Maps,
    @SerializedName("name")
    val name: String,
    @SerializedName("plaintext")
    val plaintext: String,
    @SerializedName("requiredAlly")
    val requiredAlly: String,
    @SerializedName("requiredChampion")
    val requiredChampion: String,
    @SerializedName("specialRecipe")
    val specialRecipe: Int,
    @SerializedName("stacks")
    val stacks: Int,
    @SerializedName("tags")
    val tags: List<String>
){
    data class Gold(
        @SerializedName("base")
        val base: Int,
        @SerializedName("purchasable")
        val purchasable: Boolean,
        @SerializedName("sell")
        val sell: Int,
        @SerializedName("total")
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