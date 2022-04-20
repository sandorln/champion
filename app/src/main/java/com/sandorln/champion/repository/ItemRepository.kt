package com.sandorln.champion.repository

import com.sandorln.champion.model.ItemData
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    fun getItemList(version: String): Flow<List<ItemData>>
}