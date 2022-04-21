package com.sandorln.champion.repository

import com.sandorln.champion.database.roomdao.SummonerSpellDao
import com.sandorln.champion.model.SummonerSpell
import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.network.SummonerSpellService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class SummonerSpellRepositoryImpl @Inject constructor(
    private val summonerSpellService: SummonerSpellService,
    private val summonerSpellDao: SummonerSpellDao
) : SummonerSpellRepository {
    lateinit var allSummonerSpellList: List<SummonerSpell>
    private val summonerSpellMutex = Mutex()
    private suspend fun initSummonerSpellList(version: String) {
        summonerSpellMutex.withLock {
            // 초기화 작업 (Local DB 에서 먼저 가져오기 및 버전 비교하기)
            if (!::allSummonerSpellList.isInitialized || version != allSummonerSpellList.firstOrNull()?.version ?: "") {
                allSummonerSpellList = summonerSpellDao.getAllSummonerSpell(version) ?: mutableListOf()
            }

            // 비어 있을 시 서버에서 값 가져오기
            if (allSummonerSpellList.isEmpty()) {
                val response = summonerSpellService.getAllSummonerSpell(version)
                response.parsingData()
                summonerSpellDao.insertSummonerSpellList(response.summonerSpellList)

                allSummonerSpellList = summonerSpellDao.getAllSummonerSpell(version) ?: mutableListOf()
            }
        }
    }

    override fun getSummonerSpellList(version: String): Flow<ResultData<List<SummonerSpell>>> =
        flow {
            try {
                emit(ResultData.Loading)
                initSummonerSpellList(version)
                emit(ResultData.Success(allSummonerSpellList))
            } catch (e: Exception) {
                emit(ResultData.Failed(e))
            }
        }.flowOn(Dispatchers.IO)
}