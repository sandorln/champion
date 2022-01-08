package com.sandorln.champion.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class ChampionInfo(
    @SerializedName("attack")
    var iAtk: Int = 0,
    @SerializedName("defense")
    var iDef: Int = 0,
    @SerializedName("magic")
    var iMagic: Int = 0,
    @SerializedName("difficulty")
    var idifficult: Int = 0
) : Serializable, Parcelable