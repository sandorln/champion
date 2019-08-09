package com.sandorln.champion.data

import com.google.gson.annotations.SerializedName

class CharacterData {
    @SerializedName("version")
    var cVersion: String = ""
    @SerializedName("id")
    var cId: String = ""
    @SerializedName("key")
    var cKey: Int = 0
    @SerializedName("name")
    var cName: String = ""
    @SerializedName("title")
    var cTitle: String = ""
    @SerializedName("blurb")
    var cblurb: String = ""
    @SerializedName("info")
    var cInfo: CharacterInfo = CharacterInfo()
    @SerializedName("image")
    var cImage: CharacterImage = CharacterImage()
    @SerializedName("tags")
    var cTags: List<String> = mutableListOf()
    @SerializedName("partype")
    var cPartType: String = ""
    @SerializedName("stats")
    var cStats: CharacterStats = CharacterStats()

    override fun toString(): String {
        return "CharacterData(cVersion='$cVersion', cId='$cId', cKey=$cKey, cName='$cName', cTitle='$cTitle', cblurb='$cblurb', cInfo=$cInfo, cImage=$cImage, cTags=$cTags, cPartType='$cPartType', cStats=$cStats)"
    }


}