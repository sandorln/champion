package com.sandorln.data.repository.version

import com.sandorln.data.util.asData
import com.sandorln.data.util.asEntity
import com.sandorln.database.dao.VersionDao
import com.sandorln.datastore.version.VersionDatasource
import com.sandorln.model.data.version.Version
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
    override val allVersionList: Flow<List<Version>> = versionDao
        .getAllVersion()
        .map { versionEntityList ->
            versionEntityList.map {
                it.asData()
            }
        }

    override suspend fun changeCurrentVersion(versionName: String) {
        versionDatasource.changeCurrentVersion(versionName)
    }

    override suspend fun refreshVersionList() {
        val serverVersionList = versionService.getVersionList()
        val localVersionList = allVersionList.firstOrNull() ?: emptyList()

//        serverVersionList.filter { version ->
//            !localVersionList.contains(version)
//        }.forEach { newVersion ->
//            versionDao.insertVersion(VersionEntity(name = newVersion))
//        }
    }

    override suspend fun getNotInitCompleteVersionList(): List<Version> {
        val serverVersionList = versionService.getVersionList()
        val localAllVersionList = versionDao.getAllVersion().firstOrNull() ?: emptyList()
        val localAllVersionNameList = localAllVersionList.map { it.name }

        val newVersionList = serverVersionList
            .filterNot { versionName ->
                localAllVersionNameList.contains(versionName)
            }.map { versionName ->
                Version(name = versionName)
            }.toMutableList()

        val localNotInitCompleteVersionList = localAllVersionList
            .filterNot {
                it.isInitCompleteVersion
            }.map {
                it.asData()
            }

        newVersionList.addAll(localNotInitCompleteVersionList)

        return newVersionList
    }

    override suspend fun updateVersionData(version: Version) {
        versionDao.insertVersion(version.asEntity())
    }
}