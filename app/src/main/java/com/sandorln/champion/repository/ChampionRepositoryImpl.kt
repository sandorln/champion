package com.sandorln.champion.repository

import com.sandorln.champion.database.roomdao.ChampionDao
import com.sandorln.champion.model.ChampionData
import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.network.ChampionService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class ChampionRepositoryImpl @Inject constructor(
    private val championService: ChampionService,
    private val championDao: ChampionDao
) : ChampionRepository {
    lateinit var allChampionList: List<ChampionData>
    private val championMutex = Mutex()

    private suspend fun initAllChampion(championVersion: String) {
        championMutex.withLock {
            if (championVersion.isEmpty())
                throw Exception("버전 정보가 없습니다")

            /* 인터넷으로 값 받아오기 */
            if (!::allChampionList.isInitialized || allChampionList.first().version != championVersion) {
                allChampionList = championDao.getAllChampion(championVersion)
            }

            /* 버전에 맞는 챔피언들이 저장이 안되어있을 시 다시 받아오기 */
            if (allChampionList.isEmpty()) {
                val response = championService.getAllChampion(championVersion)
                response.parsingData()
                championDao.insertChampionList(response.championList)
                allChampionList = championDao.getAllChampion(championVersion)
            }
        }
    }

    override fun getChampionList(championVersion: String, search: String): Flow<ResultData<List<ChampionData>>> = flow {
        try {
            emit(ResultData.Loading)
            initAllChampion(championVersion)

            /* 검색어에 맞는 챔피언 필터 */
            val searchChampionList = allChampionList.filter { champion ->
                /* 검색어 / 검색 대상 공백 제거 */
                champion.name.replace(" ", "").startsWith(search.replace(" ", ""))
            }

            emit(ResultData.Success(searchChampionList))
        } catch (e: Exception) {
            emit(ResultData.Failed(e, data = championDao.getAllChampion(championVersion)))
        }
    }.flowOn(Dispatchers.IO)

    override fun getChampionInfo(championVersion: String, championId: String): Flow<ResultData<ChampionData>> = flow {
        try {
            val response = championService.getChampionDetailInfo(championVersion, championId)
            response.parsingData()
            emit(ResultData.Success(response.championList.first().apply { version = championVersion }))
        } catch (e: Exception) {
            emit(ResultData.Failed(e))
        }
    }.flowOn(Dispatchers.IO)
}