package com.sandorln.data.repository.champion

import com.sandorln.data.util.asData
import com.sandorln.data.util.asEntity
import com.sandorln.database.dao.ChampionDao
import com.sandorln.database.model.ChampionEntity
import com.sandorln.datastore.version.VersionDatasource
import com.sandorln.model.data.champion.SummaryChampion
import com.sandorln.network.service.ChampionService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultChampionRepository @Inject constructor(
    versionDatasource: VersionDatasource,
    private val championDao: ChampionDao,
    private val championService: ChampionService
) : ChampionRepository {
    @OptIn(ExperimentalCoroutinesApi::class)
    override val currentSummaryChampionList: Flow<List<SummaryChampion>> = versionDatasource
        .currentVersion
        .flatMapLatest { version ->
            championDao.getChampionList(version).map { entityList ->
                entityList.map(ChampionEntity::asData)
            }
        }.flowOn(Dispatchers.IO)

    override suspend fun refreshChampionList(version: String): Result<Any> = runCatching {
        val response = championService.getAllChampionDataMap(version)
        val championEntityList = response.values.map { it.asEntity(version = version) }
        championDao.insertChampionList(championEntityList)
    }

    override suspend fun getSummaryChampionListByVersion(version: String): List<SummaryChampion> =
        championDao.getChampionList(version).firstOrNull()?.map(ChampionEntity::asData) ?: emptyList()
}