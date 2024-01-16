package com.sandorln.data.repository.version

import com.sandorln.database.dao.VersionDao
import com.sandorln.database.model.VersionEntity
import com.sandorln.datastore.version.VersionDatasource
import com.sandorln.network.service.VersionService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultVersionRepository @Inject constructor(
    private val versionService: VersionService,
    private val versionDatasource: VersionDatasource,
    private val versionDao: VersionDao
) : VersionRepository {
    override val currentVersion: Flow<String> = versionDatasource.currentVersion
    override val allVersionList: Flow<List<String>> = versionDao
        .getAllVersion()
        .map { versionEntityList ->
            versionEntityList.map { versionEntity ->
                versionEntity.version
            }
        }

    override suspend fun changeCurrentVersion(version: String) {
        versionDatasource.changeCurrentVersion(version)
    }

    override suspend fun refreshVersionList() {
        val serverVersionList = versionService.getVersionList()
        val localVersionList = allVersionList.firstOrNull() ?: emptyList()

        serverVersionList.filter { version ->
            !localVersionList.contains(version)
        }.forEach { newVersion ->
            versionDao.insertVersion(VersionEntity(version = newVersion))
        }
    }
}