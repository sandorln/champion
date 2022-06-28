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
    private suspend fun <T> initAllChampion(championVersion: String, languageCode: String, getChampionData: suspend () -> T): T =
        withContext(Dispatchers.IO) {
            championMutex.withLock {
                if (championVersion.isEmpty())
                    throw Exception("버전 정보가 없습니다")

                /* 먼저 로컬에 있는 챔피언 정보 가져오기 */
                if (!::allChampionList.isInitialized ||
                    (allChampionList.firstOrNull()?.version ?: "") != championVersion ||
                    (allChampionList.firstOrNull()?.languageCode ?: "") != languageCode
                )
                    allChampionList = championDao.getAllChampion(championVersion, languageCode)

                /* 버전에 맞는 챔피언들이 저장이 안되어있을 시 서버에서 다시 받아오기 */
                if (allChampionList.isEmpty()) {
                    val response = championService.getAllChampion(championVersion, languageCode)
                    response.parsingData(languageCode)
                    championDao.insertChampionList(response.championList)

                    val serverChampionVersion = response.championList.firstOrNull()?.version ?: championVersion
                    /* 다음번에 토탈 버전과 챔피언 버전을 매칭할 수 있도록 저장 */
                    versionDao.insertChampionVersion(championVersion, serverChampionVersion)
                    allChampionList = championDao.getAllChampion(serverChampionVersion, languageCode)
                }
            }
            getChampionData()
        }

    override suspend fun getChampionList(championVersion: String, search: String, languageCode: String): List<ChampionData> = initAllChampion(championVersion, languageCode) {
        val searchChampionName = search.replace(" ", "").lowercase()
        /* 검색어에 맞는 챔피언 필터 */
        allChampionList.filter { champion ->
            /* 검색어 / 검색 대상 공백 제거 */
            val championName = champion.name.replace(" ", "").lowercase()
            championName.contains(searchChampionName)
        }
    }

    override suspend fun getChampionInfo(championVersion: String, championId: String, languageCode: String): ChampionData = withContext(Dispatchers.IO) {
        val response = championService.getChampionDetailInfo(championVersion, championId, languageCode)
        response.parsingData()
        response.championList.first().copy(version = championVersion)
    }
}