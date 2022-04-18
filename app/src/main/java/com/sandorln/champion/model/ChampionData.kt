package com.sandorln.champion.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class ChampionData(
    var version: String = "",
    var id: String = "",
    var key: Int = 0,
    var name: String = "",
    var title: String = "",
    var blurb: String = "",
    var info: ChampionInfo = ChampionInfo(),
    var image: LOLImage = LOLImage(),
    var tags: List<String> = mutableListOf(),
    var partype: String = "",
    var stats: ChampionStats = ChampionStats(),
    var skins: List<ChampionSkin> = mutableListOf(),

    var spells: List<ChampionSpell> = mutableListOf(),
    var passive: ChampionSpell = ChampionSpell(),

    var allytips: List<String> = mutableListOf(),
    var enemytips: List<String> = mutableListOf()
) : Serializable, Parcelable