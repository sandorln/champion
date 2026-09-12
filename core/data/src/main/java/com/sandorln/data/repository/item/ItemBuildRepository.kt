package com.sandorln.data.repository.item

import com.sandorln.model.data.item.ItemBuild
import kotlinx.coroutines.flow.Flow

interface ItemBuildRepository {
    fun getItemBuildListByVersion(version: String): Flow<List<ItemBuild>>
    fun getItemBuildById(id: Long): Flow<ItemBuild?>
    suspend fun getItemBuildCountByVersion(version: String): Int
    suspend fun saveItemBuild(itemBuild: ItemBuild): Long
    suspend fun deleteItemBuild(id: Long)
}
