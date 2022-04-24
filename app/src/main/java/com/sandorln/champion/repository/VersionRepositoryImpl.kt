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
import kotlinx.coroutines.withContext
import javax.inject.Inject

class VersionRepositoryImpl @Inject constructor(
    private val versionService: VersionService,
    private val versionDao: VersionDao
) : VersionRepository {
    private lateinit var initVersionCategory: VersionCategory
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

    /* 버전 카테고리 없애고, 통합 버전으로 변경 */
    private var initVersionList: List<String> = mutableListOf()
    private val versionMutex = Mutex()
    private suspend fun <T> initLolVersion(getVersionData: suspend () -> T): T =
        withContext(Dispatchers.IO) {
            versionMutex.withLock {
                initVersionList.ifEmpty {
                    try {
                        /* 로컬에서 값 가져오기 */
                        val localVersionList = versionDao.getTotalVersionList()
                        /* 서버에서 값 가져오기 */
                        val serverVersionList = versionService.getVersionList()
                        initVersionList = when {
                            /* DB에 저장되어 있는 정보가 없었을 시 */
                            localVersionList.isEmpty() ||
                                    /* 로컬 정보와 서버 정보가 달라졌을 시 */
                                    !localVersionList.containsAll(serverVersionList) -> {
                                versionDao.insertTotalVersionList(serverVersionList)
                                versionDao.insertTotalVersion(serverVersionList.first())
                                serverVersionList
                            }
                            else -> localVersionList
                        }
                    } catch (e: Exception) {
                        /* 만약 서버에 접속할 수 없다면 Local DB 에서 가져오기 */
                        initVersionList = versionDao.getTotalVersionList()
                    }
                }
            }

            getVersionData()
        }


    override suspend fun getLolVersion(): String = initLolVersion {
        versionDao.getTotalVersion()
    }

    override suspend fun changeLolVersion(version: String) {
        versionDao.insertTotalVersion(version)
    }

    override suspend fun getLolVersionList(): List<String> = initLolVersion {
        initVersionList
    }

    override suspend fun getLolChampionVersion(totalVersion: String): String = try {
        versionDao.getChampionVersion(totalVersion)
    } catch (e: Exception) {
        totalVersion
    }

}