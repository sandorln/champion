package com.sandorln.champion.repository

import com.sandorln.champion.database.roomdao.SummonerSpellDao
import com.sandorln.champion.database.shareddao.VersionDao
import com.sandorln.model.SummonerSpell
import com.sandorln.champion.network.SummonerSpellService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SummonerSpellRepositoryImpl @Inject constructor(
    private val summonerSpellService: SummonerSpellService,
    private val summonerSpellDao: SummonerSpellDao,
    private val versionDao: VersionDao
) : SummonerSpellRepository {
    private lateinit var allSummonerSpellList: List<com.sandorln.model.SummonerSpell>
    private val summonerSpellMutex = Mutex()
    private suspend fun <T> initSummonerSpellList(
        totalVersion: String,
        languageCode: String,
        getSummonerSpellData: suspend () -> T
    ): T =
        withContext(Dispatchers.IO) {
            summonerSpellMutex.withLock {
                if (totalVersion.isEmpty())
                    throw Exception("버전 정보가 없습니다")

                val summonerSpellVersion = versionDao.getSummonerSpellVersion(totalVersion)

                // 초기화 작업 (Local DB 에서 먼저 가져오기 및 버전 비교하기)
                if (!::allSummonerSpellList.isInitialized ||
                    (allSummonerSpellList.firstOrNull()?.version ?: "") != summonerSpellVersion ||
                    (allSummonerSpellList.firstOrNull()?.languageCode ?: "") != languageCode
                ) {
                    allSummonerSpellList = summonerSpellDao.getAllSummonerSpell(summonerSpellVersion, languageCode) ?: mutableListOf()
                }

                // 비어 있을 시 서버에서 값 가져오기
                if (allSummonerSpellList.isEmpty()) {
                    val response = summonerSpellService.getAllSummonerSpell(totalVersion, languageCode)
                    response.parsingData(languageCode = languageCode)
                    summonerSpellDao.insertSummonerSpellList(response.summonerSpellList)

                    val serverSummonerSpellVersion = response.summonerSpellList.firstOrNull()?.version ?: totalVersion
                    /* 다음번에 토탈 버전과 스펠 버전을 매칭할 수 있도록 저장 */
                    versionDao.insertSummonerSpellVersion(totalVersion, serverSummonerSpellVersion)
                    allSummonerSpellList = summonerSpellDao.getAllSummonerSpell(serverSummonerSpellVersion, languageCode) ?: mutableListOf()
                }
            }

            getSummonerSpellData()
        }

    override suspend fun getSummonerSpellList(
        summonerSpellVersion: String,
        languageCode: String
    ): List<com.sandorln.model.SummonerSpell> = initSummonerSpellList(
        totalVersion = summonerSpellVersion,
        languageCode = languageCode
    ) {
        allSummonerSpellList
    }
}