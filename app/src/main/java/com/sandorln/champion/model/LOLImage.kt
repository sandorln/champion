package com.sandorln.champion.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class LOLImage(
    @SerializedName("full")
    var imgFull: String = "",
    @SerializedName("sprite")
    var imgSprite: String = "",
    @SerializedName("group")
    var imgGroup: String = "",
    @SerializedName("x")
    var imgX: Int = 0,
    @SerializedName("y")
    var imgY: Int = 0,
    @SerializedName("w")
    var imgW: Int = 0,
    @SerializedName("h")
    var imgH: Int = 0
) : Serializable, Parcelable