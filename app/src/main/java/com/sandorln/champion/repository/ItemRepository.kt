package com.sandorln.champion.repository

import com.sandorln.champion.model.ItemData

interface ItemRepository {
    suspend fun getItemList(totalVersion: String, search: String, inStore: Boolean): List<ItemData>
    suspend fun findItemById(totalVersion: String, itemId: String): ItemData
}