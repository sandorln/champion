package com.sandorln.data.repository.item

import com.sandorln.data.util.asData
import com.sandorln.data.util.asEntity
import com.sandorln.database.dao.ItemDao
import com.sandorln.database.model.ItemEntity
import com.sandorln.database.model.SummaryItemEntity
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
import javax.inject.Inject

class DefaultItemRepository @Inject constructor(
    versionDatasource: VersionDatasource,
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

    override suspend fun refreshItemList(version: String): Result<Any> = runCatching {
        val response = itemService.getAllItemMap(version)
        val itemEntityList = response.map { it.value.asEntity(id = it.key, version = version) }
        itemDao.insertItemDataList(itemEntityList)
    }.onFailure {
    }

    override suspend fun getItemListByVersion(version: String): List<ItemData> =
        itemDao.getAllItemData(version).firstOrNull()?.map(ItemEntity::asData) ?: emptyList()

    override suspend fun getNewItemIdList(versionName: String, preVersionName: String): List<String> =
        itemDao.getNewItemIdList(versionName, preVersionName)

    override suspend fun getItemDataByIdAndVersion(id: String, versionName: String): ItemData? =
        itemDao.getItemDataByIdAndVersion(versionName, id).firstOrNull()?.asData()
}