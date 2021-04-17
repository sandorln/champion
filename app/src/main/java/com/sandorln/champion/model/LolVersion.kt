package com.sandorln.champion.model

import com.google.gson.annotations.SerializedName

data class LolVersion(
    @SerializedName("n")
    var lvCategory: CategoryVersion,
    @SerializedName("v")
    var lvTotalVersion: String = "",

    @SerializedName("l")
    var lvLanguage: String = "",

    @SerializedName("cdn")
    var lvCdn: String = "",

    @SerializedName("dd")
    var lvDd: String = "",

    @SerializedName("lg")
    var lvLg: String = "",

    @SerializedName("css")
    var lvCss: String = "",

    @SerializedName("profileiconmax")
    var lvProfileiconMax: String = "",

    @SerializedName("store")
    var lvStore: String = "",
) {
    inner class CategoryVersion {
        @SerializedName("item")
        var cvItem: String = ""

        @SerializedName("rune")
        var cvRune: String = ""

        @SerializedName("mastery")
        var cvMastery: String = ""

        @SerializedName("summoner")
        var cvSummoner: String = ""

        @SerializedName("champion")
        var cvChampion: String = ""

        @SerializedName("profileicon")
        var cvProfileicon: String = ""

        @SerializedName("map")
        var cvMap: String = ""

        @SerializedName("language")
        var cvLanguage: String = ""

        @SerializedName("sticker")
        var cvSticker: String = ""
    }
}