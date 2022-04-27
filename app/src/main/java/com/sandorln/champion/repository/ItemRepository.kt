package com.sandorln.champion.repository

import com.sandorln.champion.model.ItemData

interface ItemRepository {
    suspend fun getItemList(itemVersion: String, search: String, inStore: Boolean): List<ItemData>
    suspend fun findItemById(itemVersion: String, itemId: String): ItemData
}