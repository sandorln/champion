package com.sandorln.network.service

import com.sandorln.network.BuildConfig
import com.sandorln.network.model.NetworkItem
import com.sandorln.network.model.response.BaseLolResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemService @Inject constructor(
    private val ktorClient: HttpClient
) {
    suspend fun getAllItemMap(version: String): Map<String, NetworkItem> {
        val response = ktorClient
            .get(BuildConfig.BASE_URL + "/cdn/${version}/data/${BuildConfig.BASE_LANGUAGE}/item.json")
            .body<BaseLolResponse<Map<String, NetworkItem>>>()

        return response.data ?: throw Exception("")
    }
}