package com.sandorln.champion.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class CharacterStats(
    @SerializedName("hp")
    var sHp: Double = 0.0,
    @SerializedName("hpperlevel")
    var sHpLv: Double = 0.0,
    @SerializedName("mp")
    var sMp: Double = 0.0,
    @SerializedName("mpperlevel")
    var sMpLv: Double = 0.0,
    @SerializedName("movespeed")
    var sMoveSpeed: Double = 0.0,
    @SerializedName("armor")
    var sArmor: Double = 0.0,
    @SerializedName("armorperlevel")
    var sArmorLv: Double = 0.0,
    @SerializedName("spellblock")
    var sSpellBlock: Double = 0.0,
    @SerializedName("spellblockperlevel")
    var sSpellBlockLv: Double = 0.0,
    @SerializedName("attackrange")
    var sAttackRange: Double = 0.0,
    @SerializedName("hpregen")
    var sHpRegen: Double = 0.0,
    @SerializedName("hpregenperlevel")
    var sHpRegenLv: Double = 0.0,
    @SerializedName("mpregen")
    var sMpRegen: Double = 0.0,
    @SerializedName("mpregenperlevel")
    var sMpRegenLv: Double = 0.0,
    @SerializedName("crit")
    var sCrit: Double = 0.0,
    @SerializedName("critperlevel")
    var sCritLv: Double = 0.0,
    @SerializedName("attackdamage")
    var sAttk: Double = 0.0,
    @SerializedName("attackdamageperlevel")
    var sAttkLv: Double = 0.0,
    @SerializedName("attackspeedoffset")
    var sAttkSpeed: Double = 0.0,
    @SerializedName("attackspeedperlevel")
    var sAttkSpeedLv: Double = 0.0
) : Serializable, Parcelable