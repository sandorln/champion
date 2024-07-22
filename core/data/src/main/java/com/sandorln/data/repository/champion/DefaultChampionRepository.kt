package com.sandorln.data.repository.champion

import com.sandorln.data.util.asData
import com.sandorln.data.util.asDetailData
import com.sandorln.data.util.asEntity
import com.sandorln.database.dao.ChampionDao
import com.sandorln.database.model.ChampionEntity
import com.sandorln.datastore.local.version.VersionDatasource
import com.sandorln.model.data.champion.ChampionDetailData
import com.sandorln.model.data.champion.ChampionPatchNote
import com.sandorln.model.data.champion.SummaryChampion
import com.sandorln.model.type.ChampionTag
import com.sandorln.network.model.champion.NetworkChampionPatchNote
import com.sandorln.network.service.ChampionService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.min

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
        val championEntityList = response.values.map {
            it.asEntity(version = version)
        }
        championDao.insertChampionList(championEntityList)
    }

    override suspend fun getSummaryChampionListByVersion(version: String): List<SummaryChampion> =
        championDao.getChampionList(version).firstOrNull()?.map(ChampionEntity::asData) ?: emptyList()

    override suspend fun getSummaryChampionListByChampionIdList(
        version: String,
        championIdList: List<String>
    ): List<SummaryChampion> =
        championDao.getChampionListByChampionIdList(
            version,
            championIdList
        ).map(ChampionEntity::asData)

    override suspend fun getNewChampionIdList(
        versionName: String,
        preVersionName: String
    ): List<String> =
        championDao.getNewChampionIdList(
            versionName,
            preVersionName
        )

    override suspend fun getChampionDetail(
        championId: String,
        version: String
    ): ChampionDetailData =
        withContext(Dispatchers.IO) {
            var championDetailData = ChampionDetailData()
            runCatching {
                championDetailData = championDao
                    .getChampionEntity(version, championId)
                    .firstOrNull()
                    ?.asDetailData() ?: throw Exception()

                val serverChampion = championService.getChampionDetail(championName = championId, version = version)
                championDetailData = serverChampion.asData(championDetailData)
            }

            championDetailData
        }

    override suspend fun hasChampionDetailData(
        championId: String,
        version: String
    ): Boolean = withContext(Dispatchers.IO) {
        championDao.hasChampionDetailData(
            version = version,
            championId = championId
        ) > 0
    }

    override suspend fun getSummaryChampion(
        championId: String,
        version: String
    ): SummaryChampion? = withContext(Dispatchers.IO) {
        championDao.getChampionEntity(
            version,
            championId
        ).firstOrNull()?.asData()
    }

    override suspend fun getSimilarChampionList(
        version: String,
        tags: List<ChampionTag>
    ): List<SummaryChampion> = withContext(Dispatchers.IO) {
        championDao
            .getSimilarChampionList(
                version,
                tags.asEntity()
            ).map(ChampionEntity::asData)
    }

    override suspend fun getChampionVersionList(championId: String): List<String> = withContext(Dispatchers.IO) {
        val versionComparator = Comparator<String> { version1, version2 ->
            val (major1, minor1, patch1) = version1.split('.').map { it.toInt() }
            val (major2, minor2, patch2) = version2.split('.').map { it.toInt() }

            when {
                major1 != major2 -> major2 - major1
                minor1 != minor2 -> minor2 - minor1
                else -> patch2 - patch1
            }
        }

        championDao.getChampionVersionList(championId).sortedWith(versionComparator)
    }

    override suspend fun getChampionDiffStatusVersion(championId: String): Map<String, Boolean> = withContext(Dispatchers.IO) {
        val championVersionList = getChampionVersionList(championId)

        championVersionList
            .mapIndexed { index, version ->
                val oldVersionIndex = min(index + 1, championVersionList.lastIndex)
                val oldVersion = championVersionList[oldVersionIndex]

                championDao.getChangedStatsVersionList(
                    id = championId,
                    newVersion = version,
                    oldVersion = oldVersion
                ).firstOrNull()
            }
            .filterNotNull()
            .associateWith { true }
    }

    override suspend fun getChampionPatchNoteList(version: String): List<ChampionPatchNote> =
        championService.getChampionPathNoteList(version).map(NetworkChampionPatchNote::asData)
}