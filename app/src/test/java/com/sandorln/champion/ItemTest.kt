package com.sandorln.champion

import com.sandorln.champion.network.ItemService
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ItemTest {

    lateinit var itemService: ItemService

    @Before
    fun before() {
        itemService = Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItemService::class.java)
    }

    @Test
    fun testItemResponse() {
        runBlocking {
            val itemVersion = "12.7.1"
            val itemResponse = itemService.getAllItem(itemVersion)
            itemResponse.parsingData(itemVersion)
            println("아이템 데이터 값 : ${itemResponse.itemList}")
        }
    }
}