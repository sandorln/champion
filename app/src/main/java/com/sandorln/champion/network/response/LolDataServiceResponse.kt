package com.sandorln.champion.network.response

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.sandorln.champion.model.CharacterData

data class LolDataServiceResponse(
    @SerializedName("type")
    var rType: String = "",
    @SerializedName("format")
    var rFormat: String = "",
    @SerializedName("version")
    var rVersion: String = "",

    /* 해당 Data 를 받아와서 별도의 Parsing 을 해줌 */
    @SerializedName("data")
    var rData: JsonObject? = JsonObject(),

    /* 위의 rData 를 Parsing 하여 생성 될 캐릭터 정보 리스트 */
    var rCharacterList: MutableList<CharacterData> = mutableListOf()
) {

    /**
     * 캐릭터 정보 값 Parsing
     */
    fun parsingData(): Boolean {
        rCharacterList.clear()

        return if (rData != null) {
            val gson = Gson()
            for ((_, value) in rData!!.entrySet()) {
                rCharacterList.add(gson.fromJson(value, CharacterData::class.java))
            }
            true
        } else
            false

    }
}