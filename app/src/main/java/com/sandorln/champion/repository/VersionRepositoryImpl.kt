package com.sandorln.champion.repository

import com.sandorln.champion.database.shareddao.VersionDao
import com.sandorln.champion.model.VersionLol.VersionCategory
import com.sandorln.champion.network.VersionService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class VersionRepositoryImpl @Inject constructor(
    private val versionService: VersionService,
    private val versionDao: VersionDao
) : VersionRepository {
    lateinit var initVersionCategory: VersionCategory
    private val versionMutex = Mutex()
    override fun getLolVersionCategory(): Flow<VersionCategory> = flow {
        versionMutex.withLock {
            try {
                if (!::initVersionCategory.isInitialized) {
                    val versionCategory = versionService.getVersion().category
                    versionDao.insertVersionCategory(versionCategory)
                    initVersionCategory = versionCategory
                }
                emit(versionDao.getVersionCategory())
            } catch (e: Exception) {
                emit(versionDao.getVersionCategory())
            }
        }
    }.flowOn(Dispatchers.IO)
}