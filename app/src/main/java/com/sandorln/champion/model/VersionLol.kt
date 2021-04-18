package com.sandorln.champion.model

import com.google.gson.annotations.SerializedName

data class VersionLol(
    @SerializedName("n")
    var lvCategory: VersionCategory = VersionCategory(),

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
)