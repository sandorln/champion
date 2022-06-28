package com.sandorln.champion.repository

import com.sandorln.champion.model.ItemData

interface ItemRepository {
    suspend fun getItemList(itemVersion: String, search: String, inStore: Boolean, languageCode: String): List<ItemData>
    suspend fun findItemById(itemVersion: String, itemId: String, languageCode: String): ItemData
}