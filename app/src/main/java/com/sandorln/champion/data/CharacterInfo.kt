package com.sandorln.champion.data

import com.google.gson.annotations.SerializedName

class CharacterInfo {
    @SerializedName("attack")
    var iAtk: Int = 0
    @SerializedName("defense")
    var iDef: Int = 0
    @SerializedName("magic")
    var iMagic: Int = 0
    @SerializedName("difficulty")
    var idifficult: Int = 0

    override fun toString(): String {
        return "CharacterInfo(iAtk=$iAtk, iDef=$iDef, iMagic=$iMagic, idifficult=$idifficult)"
    }


}