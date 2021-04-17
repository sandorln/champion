package com.sandorln.champion.model

import com.google.gson.annotations.SerializedName

data class CharacterData(
    @SerializedName("version")
    var cVersion: String = "",
    @SerializedName("id")
    var cId: String = "",
    @SerializedName("key")
    var cKey: Int = 0,
    @SerializedName("name")
    var cName: String = "",
    @SerializedName("title")
    var cTitle: String = "",
    @SerializedName("blurb")
    var cBlurb: String = "",
    @SerializedName("info")
    var cInfo: CharacterInfo = CharacterInfo(),
    @SerializedName("image")
    var cImage: CharacterImage = CharacterImage(),
    @SerializedName("tags")
    var cTags: List<String> = mutableListOf(),
    @SerializedName("partype")
    var cPartType: String = "",
    @SerializedName("stats")
    var cStats: CharacterStats = CharacterStats(),
    @SerializedName("skins")
    var cSkins: List<CharacterSkin> = mutableListOf(),
    @SerializedName("allytips")
    var cAllytips: List<String> = mutableListOf(),
    @SerializedName("enemytips")
    var cEnemytips: List<String> = mutableListOf()
)