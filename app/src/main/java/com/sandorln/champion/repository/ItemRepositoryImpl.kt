package com.sandorln.champion.repository

import com.sandorln.champion.model.ItemData
import com.sandorln.champion.network.ItemService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ItemRepositoryImpl @Inject constructor(
    private val itemService: ItemService
)  {

//    override fun getItemList(version: String): Flow<List<ItemData>> {
//
//    }
}