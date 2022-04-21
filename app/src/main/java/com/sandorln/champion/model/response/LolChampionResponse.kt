package com.sandorln.champion.model.response

import com.google.gson.Gson
import com.sandorln.champion.model.ChampionData

data class LolChampionResponse(
    var format: String = "",

    /* 위의 data 를 Parsing 하여 생성 될 캐릭터 정보 리스트 */
    var championList: MutableList<ChampionData> = mutableListOf()
) : BaseLolResponse() {
    /**
     * 캐릭터 정보 값 Parsing
     */
    override fun parsingData() {
        championList.clear()

        val gson = Gson()
        for ((_, value) in data.entrySet())
            championList.add(gson.fromJson(value, ChampionData::class.java))
    }
}
