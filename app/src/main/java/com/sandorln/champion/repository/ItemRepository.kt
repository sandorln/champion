package com.sandorln.champion.repository

import com.sandorln.champion.model.ItemData
import com.sandorln.champion.model.result.ResultData
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    fun getItemList(version: String, search: String, inStore: Boolean): Flow<ResultData<List<ItemData>>>
}