package com.sandorln.champion.model.response

import com.google.gson.Gson
import com.sandorln.champion.model.ItemData

data class LolItemResponse(
    /* 위의 data 를 Parsing 하여 생성 될 아이템 정보 리스트 */
    var itemList: MutableList<ItemData> = mutableListOf()
) : BaseLolResponse() {
    /**
     * 아이템 정보 값 Parsing
     */
    override fun parsingData(languageCode: String) {
        itemList = mutableListOf()
        val itemVersion = version

        val gson = Gson()
        for ((id, value) in data.entrySet()) {
            val itemData = gson.fromJson(value, ItemData::class.java).apply {
                this.id = id
                this.version = itemVersion
            }
            itemList.add(itemData)
        }
    }
}