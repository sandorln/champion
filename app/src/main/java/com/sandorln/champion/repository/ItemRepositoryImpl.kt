package com.sandorln.champion.repository

import com.sandorln.champion.database.roomdao.ItemDao
import com.sandorln.champion.model.ItemData
import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.network.ItemService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class ItemRepositoryImpl @Inject constructor(
    private val itemService: ItemService,
    private val itemDao: ItemDao,
) : ItemRepository {
    lateinit var initAllItemList: List<ItemData>
    private val itemMutex = Mutex()

    override fun getItemList(version: String): Flow<ResultData<List<ItemData>>> = flow {
        itemMutex.withLock {
            try {
                emit(ResultData.Loading)

                // 초기화 작업 (Local DB 에서 먼저 가져오기)
                if (!::initAllItemList.isInitialized) {
                    initAllItemList = itemDao.getAllItemData(version) ?: mutableListOf()
                }

                // 비어 있을 시 서버에서 값 가져오기
                if (initAllItemList.isEmpty()) {
                    val response = itemService.getAllItem(version)
                    response.parsingData(version)
                    itemDao.insertItemDataList(response.itemList)

                    initAllItemList = itemDao.getAllItemData(version) ?: mutableListOf()
                }

                itemDao.insertItemDataList(initAllItemList)
                emit(ResultData.Success(itemDao.getAllItemData(version)))
            } catch (e: Exception) {
                emit(ResultData.Failed(e, itemDao.getAllItemData(version)))
            }
        }
    }.flowOn(Dispatchers.IO)
}