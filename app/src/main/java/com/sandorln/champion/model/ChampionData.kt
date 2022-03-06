package com.sandorln.champion.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
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
    var cInfo: ChampionInfo = ChampionInfo(),
    @SerializedName("image")
    var cImage: LOLImage = LOLImage(),
    @SerializedName("tags")
    var cTags: List<String> = mutableListOf(),
    @SerializedName("partype")
    var cPartType: String = "",
    @SerializedName("stats")
    var cStats: ChampionStats = ChampionStats(),
    @SerializedName("skins")
    var cSkins: List<ChampionSkin> = mutableListOf(),

    @SerializedName("spells")
    var cSpellList: List<ChampionSpell> = mutableListOf(),
    @SerializedName("passive")
    var cPassive: ChampionSpell = ChampionSpell(),

    @SerializedName("allytips")
    var cAllytips: List<String> = mutableListOf(),
    @SerializedName("enemytips")
    var cEnemytips: List<String> = mutableListOf()
) : Serializable, Parcelable