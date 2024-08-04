package com.sandorln.data.repository.item

import com.sandorln.data.util.asCombinationData
import com.sandorln.data.util.asData
import com.sandorln.data.util.asEntity
import com.sandorln.database.dao.ItemDao
import com.sandorln.database.model.ItemEntity
import com.sandorln.datastore.local.version.VersionDatasource
import com.sandorln.model.data.item.ItemCombination
import com.sandorln.model.data.item.ItemData
import com.sandorln.model.data.item.ItemPatchNote
import com.sandorln.model.data.item.SummaryItemImage
import com.sandorln.network.model.item.NetworkItemPatchNote
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
            itemDao.getAllItemData(version).map { entityList ->
                entityList.map(ItemEntity::asData)
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

    override suspend fun getItemDataByIdAndVersion(id: String, versionName: String): ItemData =
        itemDao.getItemDataByIdAndVersion(versionName, id).firstOrNull()?.asData() ?: throw Exception("")

    override suspend fun getItemCombination(id: String, version: String): ItemCombination {
        val baseItemCombination = itemDao.getItemDataByIdAndVersion(version, id).first()

        return if (baseItemCombination.from.isEmpty()) {
            baseItemCombination.asCombinationData(emptyList())
        } else {
            val itemCombinationList = baseItemCombination.from.map { getItemCombination(it, version) }
            baseItemCombination.asCombinationData(itemCombinationList)
        }
    }

    override suspend fun getSummaryItemImage(id: String, versionName: String): SummaryItemImage? =
        itemDao.getSummaryItemImage(id, versionName).firstOrNull()?.asData()

    override suspend fun getItemPatchList(version: String): List<ItemPatchNote> =
        itemService.getItemPathNoteList(version).map(NetworkItemPatchNote::asData)
}