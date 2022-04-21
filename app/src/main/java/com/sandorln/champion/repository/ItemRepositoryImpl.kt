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
    lateinit var allItemList: List<ItemData>
    private val itemMutex = Mutex()
    private suspend fun initAllItemList(version: String) {
        itemMutex.withLock {
            // 초기화 작업 (Local DB 에서 먼저 가져오기 및 버전 비교하기)
            if (!::allItemList.isInitialized || version != allItemList.firstOrNull()?.version ?: "") {
                allItemList = itemDao.getAllItemData(version) ?: mutableListOf()
            }

            // 비어 있을 시 서버에서 값 가져오기
            if (allItemList.isEmpty()) {
                val response = itemService.getAllItem(version)
                response.parsingData()
                itemDao.insertItemDataList(response.itemList)

                allItemList = itemDao.getAllItemData(version) ?: mutableListOf()
            }
        }
    }

    override fun getItemList(version: String, search: String, inStore: Boolean): Flow<ResultData<List<ItemData>>> =
        flow {
            try {
                emit(ResultData.Loading)
                initAllItemList(version)

                /* 검색어 / 검색 대상 공백 제거 */
                val itemSearch = search.replace(" ", "").uppercase()

                /* 검색어에 맞는 아이템 필터 */
                val searchItemList = allItemList.filter { item ->
                    val isSearchName = item
                        .name
                        .uppercase()
                        .replace(" ", "")
                        .contains(itemSearch)

                    if (inStore)
                        isSearchName && item.inStore == inStore
                    else
                        isSearchName
                }.sortedBy { it.gold.total }


                emit(ResultData.Success(searchItemList))
            } catch (e: Exception) {
                emit(ResultData.Failed(e, itemDao.getAllItemData(version)))
            }
        }.flowOn(Dispatchers.IO)


    override fun findItemById(version: String, itemId: String): Flow<ResultData<ItemData>> =
        flow {
            try {
                emit(ResultData.Loading)
                initAllItemList(version)

                val findItem = allItemList.firstOrNull { it.id == itemId } ?: throw Exception("해당 아이템은 존재하지 않습니다")
                emit(ResultData.Success(findItem))
            } catch (e: Exception) {
                emit(ResultData.Failed(e))
            }
        }.flowOn(Dispatchers.IO)
}