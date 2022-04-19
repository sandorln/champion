package com.sandorln.champion.model.response

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.sandorln.champion.model.ChampionData

data class LolChampionResponse(
    var type: String = "",
    var format: String = "",
    var version: String = "",

    /* 해당 Data 를 받아와서 별도의 Parsing 을 해줌 */
    var data: JsonObject? = JsonObject(),

    /* 위의 rData 를 Parsing 하여 생성 될 캐릭터 정보 리스트 */
    var championList: MutableList<ChampionData> = mutableListOf()
) {
    /**
     * 캐릭터 정보 값 Parsing
     */
    fun parsingData() {
        championList.clear()

        if (data != null) {
            val gson = Gson()
            for ((_, value) in data!!.entrySet())
                championList.add(gson.fromJson(value, ChampionData::class.java))
        } else
            throw Exception("값을 찾을 수 없습니다")
    }
}
