package com.sandorln.champion.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

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
