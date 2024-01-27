package com.sandorln.data.repository.item

import com.sandorln.model.data.item.ItemData
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    val currentItemList: Flow<List<ItemData>>

    suspend fun refreshItemList(version: String): Result<Any>

    suspend fun getItemListByVersion(version: String): List<ItemData>

    suspend fun getNewItemListByCurrentVersion(currentVersionName: String, preVersionName: String) : List<ItemData>
}