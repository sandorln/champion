package com.sandorln.champion.repository

import com.sandorln.model.ItemData

interface ItemRepository {
    suspend fun getItemList(itemVersion: String, search: String, inStore: Boolean, languageCode: String): List<com.sandorln.model.ItemData>
    suspend fun findItemById(itemVersion: String, itemId: String, languageCode: String): com.sandorln.model.ItemData
}