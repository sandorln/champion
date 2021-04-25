package com.sandorln.champion.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class ChampionData(
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
    var cSkins: List<ChampionSkin> = mutableListOf(),
    @SerializedName("allytips")
    var cAllytips: List<String> = mutableListOf(),
    @SerializedName("enemytips")
    var cEnemytips: List<String> = mutableListOf()
) : Serializable, Parcelable