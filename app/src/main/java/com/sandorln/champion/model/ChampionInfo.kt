package com.sandorln.champion.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class ChampionInfo(
    var attack: Int = 0,
    var defense: Int = 0,
    var magic: Int = 0,
    var difficulty: Int = 0
) : Serializable, Parcelable