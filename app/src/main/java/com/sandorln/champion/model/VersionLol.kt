package com.sandorln.champion.model

import com.google.gson.annotations.SerializedName

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
)