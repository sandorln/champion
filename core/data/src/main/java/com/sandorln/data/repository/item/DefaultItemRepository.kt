package com.sandorln.data.repository.item

import com.sandorln.data.util.asData
import com.sandorln.data.util.asEntity
import com.sandorln.database.dao.ItemDao
import com.sandorln.database.dao.VersionDao
import com.sandorln.database.dao.getAllVersionOrderByDesc
import com.sandorln.database.model.ItemEntity
import com.sandorln.database.model.SummaryItemEntity
import com.sandorln.database.model.VersionEntity
import com.sandorln.datastore.version.VersionDatasource
import com.sandorln.model.data.item.ItemData
import com.sandorln.network.service.ItemService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

class DefaultItemRepository @Inject constructor(
    versionDatasource: VersionDatasource,
    versionDao: VersionDao,
    private val itemDao: ItemDao,
    private val itemService: ItemService
) : ItemRepository {
    @OptIn(ExperimentalCoroutinesApi::class)
    override val currentItemList: Flow<List<ItemData>> = versionDatasource
        .currentVersion
        .flatMapLatest { version ->
            itemDao.getAllSummaryItemData(version).map { entityList ->
                entityList.map(SummaryItemEntity::asData)
            }
        }.flowOn(Dispatchers.IO)

    private var allVersionList: List<VersionEntity> = mutableListOf()

    @OptIn(ExperimentalCoroutinesApi::class)
    override val currentNewItemList: Flow<List<ItemData>> = currentItemList
        .mapLatest { currentItemList ->
            if (allVersionList.isEmpty()) {
                allVersionList = versionDao.getAllVersionOrderByDesc()
            }

            val currentVersionName = versionDatasource.currentVersion.firstOrNull() ?: ""
            val currentIndex = allVersionList.indexOfFirst { it.name == currentVersionName }
            val preVersionName = runCatching { allVersionList[currentIndex + 1] }.getOrNull()?.name ?: ""
            val preVersionList = itemDao.getAllSummaryItemData(preVersionName).firstOrNull()?.map(SummaryItemEntity::asData) ?: emptyList()

            currentItemList.filter { currentItem ->
                preVersionList.none { preVersionItem ->
                    currentItem.id == preVersionItem.id || currentItem.name == preVersionItem.name
                }
            }
        }.flowOn(Dispatchers.IO)

    override suspend fun refreshItemList(version: String): Result<Any> = runCatching {
        val response = itemService.getAllItemMap(version)
        val itemEntityList = response.map { it.value.asEntity(id = it.key, version = version) }
        itemDao.insertItemDataList(itemEntityList)
    }.onFailure {
    }

    override suspend fun getItemListByVersion(version: String): List<ItemData> =
        itemDao.getAllItemData(version).firstOrNull()?.map(ItemEntity::asData) ?: emptyList()
}