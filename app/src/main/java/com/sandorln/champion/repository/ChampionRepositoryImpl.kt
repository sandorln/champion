package com.sandorln.champion.repository

import com.sandorln.champion.database.roomdao.ChampionDao
import com.sandorln.champion.database.shareddao.VersionDao
import com.sandorln.champion.model.ChampionData
import com.sandorln.champion.network.ChampionService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChampionRepositoryImpl @Inject constructor(
    private val championService: ChampionService,
    private val championDao: ChampionDao,
    private val versionDao: VersionDao
) : ChampionRepository {
    lateinit var allChampionList: List<ChampionData>
    private val championMutex = Mutex()

    private suspend fun <T> initAllChampion(totalVersion: String, getChampionData: suspend () -> T): T =
        withContext(Dispatchers.IO) {
            championMutex.withLock {
                if (totalVersion.isEmpty())
                    throw Exception("버전 정보가 없습니다")

                val championVersion = versionDao.getChampionVersion(totalVersion)

                /* 먼저 로컬에 있는 챔피언 정보 가져오기 */
                if (!::allChampionList.isInitialized || allChampionList.firstOrNull()?.version ?: "" != championVersion)
                    allChampionList = championDao.getAllChampion(championVersion)

                /* 버전에 맞는 챔피언들이 저장이 안되어있을 시 서버에서 다시 받아오기 */
                if (allChampionList.isEmpty()) {
                    val response = championService.getAllChampion(totalVersion)
                    response.parsingData()
                    championDao.insertChampionList(response.championList)

                    val serverChampionVersion = response.championList.firstOrNull()?.version ?: totalVersion
                    /* 다음번에 토탈 버전과 챔피언 버전을 매칭할 수 있도록 저장 */
                    versionDao.insertChampionVersion(totalVersion, serverChampionVersion)
                    allChampionList = championDao.getAllChampion(serverChampionVersion)
                }
            }
            getChampionData()
        }

    override suspend fun getChampionList(totalVersion: String, search: String): List<ChampionData> = initAllChampion(totalVersion) {
        /* 검색어에 맞는 챔피언 필터 */
        allChampionList.filter { champion ->
            /* 검색어 / 검색 대상 공백 제거 */
            champion.name.replace(" ", "").startsWith(search.replace(" ", ""))
        }
    }

    override suspend fun getChampionInfo(championVersion: String, championId: String): ChampionData = withContext(Dispatchers.IO) {
        val response = championService.getChampionDetailInfo(championVersion, championId)
        response.parsingData()
        response.championList.first().copy(version = championVersion)
    }
}