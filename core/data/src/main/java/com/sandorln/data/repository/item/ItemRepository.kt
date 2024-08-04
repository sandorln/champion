package com.sandorln.data.repository.item

import com.sandorln.model.data.item.ItemCombination
import com.sandorln.model.data.item.ItemData
import com.sandorln.model.data.item.ItemPatchNote
import com.sandorln.model.data.item.SummaryItemImage
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    val currentItemList: Flow<List<ItemData>>

    suspend fun refreshItemList(version: String): Result<Any>

    suspend fun getItemListByVersion(version: String): List<ItemData>

    suspend fun getNewItemIdList(versionName: String, preVersionName: String): List<String>

    suspend fun getItemDataByIdAndVersion(id: String, versionName: String): ItemData

    suspend fun getItemCombination(id: String, version: String): ItemCombination

    suspend fun getSummaryItemImage(id: String, versionName: String): SummaryItemImage?

    suspend fun getItemPatchList(version: String): List<ItemPatchNote>
}