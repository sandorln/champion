package com.sandorln.champion.model.response

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.sandorln.champion.model.ItemData

data class LolItemResponse(
    val type: String,
    val version: String,

    /* 해당 Data 를 받아와서 별도의 Parsing 을 해줌 */
    val data: JsonObject? = JsonObject(),

    /* 위의 data 를 Parsing 하여 생성 될 아이템 정보 리스트 */
    var itemList: MutableList<ItemData> = mutableListOf()
) {
    /**
     * 아이템 정보 값 Parsing
     */
    fun parsingData(version: String) {
        itemList = mutableListOf()

        if (data != null) {
            val gson = Gson()
            for ((id, value) in data.entrySet()) {
                val itemData = gson.fromJson(value, ItemData::class.java).apply {
                    this.id = id
                    this.version = version
                }
                itemList.add(itemData)
            }
        } else
            throw Exception("값을 찾을 수 없습니다")
    }
}