package com.sandorln.champion.repository

import com.sandorln.champion.database.roomdao.ItemDao
import com.sandorln.champion.database.shareddao.VersionDao
import com.sandorln.champion.model.ItemData
import com.sandorln.champion.network.ItemService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ItemRepositoryImpl @Inject constructor(
    private val itemService: ItemService,
    private val itemDao: ItemDao,
    private val versionDao: VersionDao
) : ItemRepository {
    lateinit var allItemList: List<ItemData>
    private val itemMutex = Mutex()
    private suspend fun <T> initAllItemList(totalVersion: String, getItemData: suspend () -> T): T =
        withContext(Dispatchers.IO) {
            itemMutex.withLock {
                if (totalVersion.isEmpty())
                    throw Exception("버전 정보가 없습니다")

                val itemVersion = versionDao.getItemVersion(totalVersion)

                // 초기화 작업 (Local DB 에서 먼저 가져오기 및 버전 비교하기)
                if (!::allItemList.isInitialized || allItemList.firstOrNull()?.version ?: "" != itemVersion) {
                    allItemList = itemDao.getAllItemData(itemVersion) ?: mutableListOf()
                }

                // 비어 있을 시 서버에서 값 가져오기
                if (allItemList.isEmpty()) {
                    val response = itemService.getAllItem(totalVersion)
                    response.parsingData()
                    itemDao.insertItemDataList(response.itemList)

                    val serverItemVersion = response.itemList.firstOrNull()?.version ?: totalVersion
                    /* 다음번에 토탈 버전과 스펠 버전을 매칭할 수 있도록 저장 */
                    versionDao.insertItemVersion(totalVersion, serverItemVersion)

                    allItemList = itemDao.getAllItemData(serverItemVersion) ?: mutableListOf()
                }
            }

            getItemData()
        }

    override suspend fun getItemList(totalVersion: String, search: String, inStore: Boolean): List<ItemData> = initAllItemList(totalVersion) {
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

        searchItemList
    }


    override suspend fun findItemById(totalVersion: String, itemId: String): ItemData = initAllItemList(totalVersion) {
        allItemList.firstOrNull { it.id == itemId } ?: throw Exception("해당 아이템은 존재하지 않습니다")
    }
}