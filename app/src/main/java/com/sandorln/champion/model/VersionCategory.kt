package com.sandorln.champion.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class VersionCategory(
    @SerializedName("item")
    var cvItem: String = "",
    @SerializedName("rune")
    var cvRune: String = "",
    @SerializedName("mastery")
    var cvMastery: String = "",
    @SerializedName("summoner")
    var cvSummoner: String = "",
    @SerializedName("champion")
    var cvChampion: String = "",
    @SerializedName("profileicon")
    var cvProfileicon: String = "",
    @SerializedName("map")
    var cvMap: String = "",
    @SerializedName("language")
    var cvLanguage: String = "",
    @SerializedName("sticker")
    var cvSticker: String = ""
) : Serializable
