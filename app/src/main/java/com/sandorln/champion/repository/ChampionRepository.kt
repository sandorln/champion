package com.sandorln.champion.repository

import com.sandorln.champion.database.roomdao.ChampionDao
import com.sandorln.champion.manager.VersionManager
import com.sandorln.champion.model.ChampionData
import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.network.ChampionService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class ChampionRepository(
    private val championService: ChampionService,
    private val championDao: ChampionDao
) {
    suspend fun refreshAllChampionList(championVersion: String) {
        /* DB에 해당 Champion Version 정보가 없을 시 값 갱신 */
        val isEmptyLocalDB = championDao.getChampionList(championVersion).first().isEmpty()
        if (isEmptyLocalDB) {
            val response = championService.getAllChampion(championVersion)
            response.parsingData()
            championDao.insertChampionList(response.rChampionList)
        }
    }

    fun getAllChampionListFlow(championVersion: String): Flow<List<ChampionData>> = championDao.getChampionList(championVersion)

    /**
     * 특정 캐릭터 정보값 가져오기
     */
    private var isLoadingGetChampion = false
    suspend fun getChampionInfo(champID: String): ResultData<ChampionData> =
        if (!isLoadingGetChampion) {
            try {
                withContext(Dispatchers.IO) {
                    isLoadingGetChampion = true
                    val response = championService.getChampionDetailInfo(VersionManager.getVersion().category.champion, champID)
                    response.parsingData()
                    ResultData.Success(response.rChampionList.first())
                }
            } catch (e: Exception) {
                ResultData.Failed(e)
            } finally {
                isLoadingGetChampion = false
            }
        } else
            ResultData.Failed(Exception("이미 로딩중 입니다"))
}