package com.sandorln.champion.model

import android.os.Parcelable
import androidx.room.Entity
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.sandorln.champion.model.type.SpellType
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
@Entity(primaryKeys = ["version", "languageCode", "id"])
data class ChampionData(
    var version: String = "",
    var languageCode: String = "",
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
) : Serializable, Parcelable {
    @Parcelize
    data class ChampionInfo(
        var attack: Int = 0,
        var defense: Int = 0,
        var magic: Int = 0,
        var difficulty: Int = 0
    ) : Serializable, Parcelable

    @Parcelize
    data class ChampionSkin(
        var id: String? = null,
        var num: String? = null,
        var name: String = "",
        var chromas: Boolean = false
    ) : Serializable, Parcelable

    @Parcelize
    data class ChampionSpell(
        var id: String = "P",
        var name: String = "",
        var description: String = "",
        var image: LOLImage = LOLImage()
    ) : Serializable, Parcelable {
        fun getSpellType(position: Int): SpellType =
            try {
                SpellType.values()[position]
            } catch (e: Exception) {
                SpellType.P
            }
    }

    @Parcelize
    data class ChampionStats(
        var hp: Double = 0.0,
        var hpperlevel: Double = 0.0,
        var mp: Double = 0.0,
        var mpperlevel: Double = 0.0,
        var movespeed: Double = 0.0,
        var armor: Double = 0.0,
        var armorperlevel: Double = 0.0,
        var spellblock: Double = 0.0,
        var spellblockperlevel: Double = 0.0,
        var attackrange: Double = 0.0,
        var hpregen: Double = 0.0,
        var hpregenperlevel: Double = 0.0,
        var mpregen: Double = 0.0,
        var mpregenperlevel: Double = 0.0,
        var crit: Double = 0.0,
        var critperlevel: Double = 0.0,
        var attackdamage: Double = 0.0,
        var attackdamageperlevel: Double = 0.0,
        var attackspeed: Double = 0.0,
        var attackspeedoffset: Double = 0.0,
        var attackspeedperlevel: Double = 0.0
    ) : Serializable, Parcelable {
        fun changeJsonArray(): JsonArray {
            val gson = Gson()
            val statusJson = gson.fromJson(gson.toJson(this), JsonObject::class.java)
            val statusArray = JsonArray()
            for ((key, value) in statusJson.entrySet()) {
                val statusJsonObject = JsonObject().apply { this.addProperty(key, value.asDouble) }
                statusArray.add(statusJsonObject)
            }
            return statusArray
        }
    }
}