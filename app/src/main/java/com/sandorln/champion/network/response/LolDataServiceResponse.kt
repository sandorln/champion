package com.sandorln.champion.network.response

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.sandorln.champion.model.ChampionData

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
    var rChampionList: MutableList<ChampionData> = mutableListOf()
) {
    /**
     * 캐릭터 정보 값 Parsing
     */
    fun parsingData() {
        rChampionList.clear()

        if (rData != null) {
            val gson = Gson()
            for ((_, value) in rData!!.entrySet())
                rChampionList.add(gson.fromJson(value, ChampionData::class.java))
        } else
            throw Exception("값을 찾을 수 없습니다")
    }
}