package com.sandorln.champion.model

import com.google.gson.annotations.SerializedName

class CharacterImage {
    @SerializedName("full")
    var imgFull: String = ""
    @SerializedName("sprite")
    var imgSprite: String = ""
    @SerializedName("group")
    var imgGroup: String = ""
    @SerializedName("x")
    var imgX: Int = 0
    @SerializedName("y")
    var imgY: Int = 0
    @SerializedName("w")
    var imgW: Int = 0
    @SerializedName("h")
    var imgH: Int = 0

    override fun toString(): String {
        return "CharacterImage(imgFull='$imgFull', imgSprite='$imgSprite', imgGroup='$imgGroup', imgX=$imgX, imgY=$imgY, imgW=$imgW, imgH=$imgH)"
    }


}