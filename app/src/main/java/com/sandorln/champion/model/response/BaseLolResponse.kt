package com.sandorln.champion.model.response

import com.google.gson.JsonObject

abstract class BaseLolResponse {
    var type: String = ""
    var version: String = ""

    /* 해당 Data 를 받아와서 별도의 Parsing 을 해줌 */
    val data: JsonObject = JsonObject()

    abstract fun parsingData()
}