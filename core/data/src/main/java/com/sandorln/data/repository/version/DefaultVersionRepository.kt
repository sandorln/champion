package com.sandorln.data.repository.version

import com.sandorln.data.util.asData
import com.sandorln.data.util.asEntity
import com.sandorln.database.dao.ChampionDao
import com.sandorln.database.dao.ItemDao
import com.sandorln.database.dao.VersionDao
import com.sandorln.database.model.VersionEntity
import com.sandorln.datastore.version.VersionDatasource
import com.sandorln.model.data.version.Version
import com.sandorln.model.data.version.VersionNewCount
import com.sandorln.network.service.VersionService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultVersionRepository @Inject constructor(
    private val versionService: VersionService,
    private val versionDatasource: VersionDatasource,
    private val versionDao: VersionDao,
    private val championDao: ChampionDao,
    private val itemDao: ItemDao
) : VersionRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override val currentVersion: Flow<Version> = versionDatasource
        .currentVersion
        .flatMapLatest { version ->
            versionDao
                .getVersionEntity(version)
                .map {
                    it.firstOrNull()?.asData() ?: Version()
                }
        }.flowOn(Dispatchers.IO)

    override val allVersionList: StateFlow<List<Version>> = versionDao
        .getAllVersion()
        .map { versionEntityList ->
            val versionComparator = Comparator<VersionEntity> { version1, version2 ->
                val (major1, minor1, patch1) = version1.name.split('.').map { it.toInt() }
                val (major2, minor2, patch2) = version2.name.split('.').map { it.toInt() }

                when {
                    major1 != major2 -> major2 - major1
                    minor1 != minor2 -> minor2 - minor1
                    else -> patch2 - patch1
                }
            }

            versionEntityList.sortedWith(versionComparator).map(VersionEntity::asData)
        }.stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.WhileSubscribed(), emptyList())

    override suspend fun changeCurrentVersion(versionName: String) {
        withContext(Dispatchers.IO) {
            versionDatasource.changeCurrentVersion(versionName)
        }
    }

    override suspend fun refreshVersionList(): Result<Any> = runCatching {
        val serverVersionList = versionService.getVersionList()
        val localAllVersionList = versionDao.getAllVersion().firstOrNull()?.map { it.name } ?: emptyList()

        serverVersionList.forEach { versionName ->
            if (!localAllVersionList.contains(versionName)) {
                versionDao.insertVersion(VersionEntity(name = versionName))
            }
        }
    }

    override suspend fun getNotInitCompleteVersionList(): List<Version> = versionDao.getNotInitVersionEntityList().map(VersionEntity::asData)
    override suspend fun updateVersionData(version: Version) {
        versionDao.insertVersion(version.asEntity())
    }
}