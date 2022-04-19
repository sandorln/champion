package com.sandorln.champion.model

import android.os.Parcelable
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

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
    var attackspeedoffset: Double = 0.0,
    var attackspeedperlevel: Double = 0.0
) : Serializable, Parcelable