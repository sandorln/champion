package com.sandorln.champion.network

import com.sandorln.champion.model.response.LolItemResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ItemService {
    @GET("/cdn/{item_version}/data/ko_KR/item.json")
    suspend fun getAllItem(@Path("item_version") itemVersion: String) : LolItemResponse
}