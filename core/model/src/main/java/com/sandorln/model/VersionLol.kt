package com.sandorln.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class VersionLol(
    @SerializedName("n")
    var category: VersionCategory = VersionCategory(),
    @SerializedName("v")
    var totalVersion: String = "",

    var language: String = "",
    var cdn: String = "",
    var dd: String = "",
    var lg: String = "",
    var css: String = "",
    var profileiconmax: String = "",
    var store: String = "",
){
    data class VersionCategory(
        var item: String = "",
        var rune: String = "",
        var mastery: String = "",
        var summoner: String = "",
        var champion: String = "",
        var profileicon: String = "",
        var map: String = "",
        var language: String = "",
        var sticker: String = ""
    ) : Serializable
}