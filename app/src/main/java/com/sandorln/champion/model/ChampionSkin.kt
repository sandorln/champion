package com.sandorln.champion.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class ChampionSkin(
    var id: String = "",
    var num: String = "",
    var name: String = "",
    var chromas: Boolean = false
) : Serializable, Parcelable
