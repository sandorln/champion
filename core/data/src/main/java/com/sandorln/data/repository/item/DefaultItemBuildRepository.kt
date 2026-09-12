package com.sandorln.data.repository.item

import com.sandorln.data.util.asData
import com.sandorln.data.util.asEntity
import com.sandorln.database.dao.ItemBuildDao
import com.sandorln.model.data.item.ItemBuild
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultItemBuildRepository @Inject constructor(
    private val itemBuildDao: ItemBuildDao
) : ItemBuildRepository {
    override fun getItemBuildListByVersion(version: String): Flow<List<ItemBuild>> =
        itemBuildDao.getItemBuildListByVersion(version).map { list -> list.map { it.asData() } }

    override fun getItemBuildById(id: Long): Flow<ItemBuild?> =
        itemBuildDao.getItemBuildById(id).map { it?.asData() }

    override suspend fun getItemBuildCountByVersion(version: String): Int =
        itemBuildDao.getItemBuildCountByVersion(version)

    override suspend fun saveItemBuild(itemBuild: ItemBuild): Long =
        itemBuildDao.insertOrUpdateItemBuild(itemBuild.asEntity())

    override suspend fun deleteItemBuild(id: Long) =
        itemBuildDao.deleteItemBuildById(id)
}
