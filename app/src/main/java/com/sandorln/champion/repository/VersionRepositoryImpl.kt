package com.sandorln.champion.repository

import com.sandorln.champion.database.shareddao.VersionDao
import com.sandorln.champion.model.VersionCategory
import com.sandorln.champion.network.VersionService
import javax.inject.Inject

class VersionRepositoryImpl @Inject constructor(
    private val versionDao: VersionDao,
    private val versionService: VersionService
) : VersionRepository {
    override suspend fun getLolVersionCategory(): VersionCategory = try {
        val versionCategory = versionService.getVersion().category
        versionDao.insertVersionCategory(versionCategory)
        versionDao.getVersionCategory()
    } catch (e: Exception) {
        versionDao.getVersionCategory()
    }
}