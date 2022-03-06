package com.sandorln.champion.repository

import com.sandorln.champion.manager.VersionManager
import com.sandorln.champion.model.ChampionData
import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.network.ChampionService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

@FlowPreview
@ExperimentalCoroutinesApi
class ChampionRepository(
    private val championService: ChampionService
) {

    /**
     * 모든 챔피언 정보 가져오기
     */
    private val inMemoryAllChampionList = mutableListOf<ChampionData>()
    private var isLoadingAllChampion = false
    private val resultAllChampionList = ConflatedBroadcastChannel<ResultData<List<ChampionData>>>()
    suspend fun getResultAllChampionList(): Flow<ResultData<List<ChampionData>>> {
        inMemoryAllChampionList.clear()
        isLoadingAllChampion = false
        requestAllChampion()
        return resultAllChampionList.asFlow()
    }

    private suspend fun requestAllChampion() {
        if (!isLoadingAllChampion) {
            try {
                isLoadingAllChampion = true
                val response = championService.getAllChampion(VersionManager.getVersion().lvCategory.cvChampion)
                response.parsingData()
                inMemoryAllChampionList.addAll(response.rChampionList.sortedBy { it.cName })
                resultAllChampionList.offer(ResultData.Success(inMemoryAllChampionList.toList()))
            } catch (e: Exception) {
                resultAllChampionList.offer(ResultData.Failed(e))
            } finally {
                isLoadingAllChampion = false
            }
        }
    }


    /**
     * 특정 캐릭터 정보값 가져오기
     */
    private var isLoadingGetChampion = false
    suspend fun getChampionInfo(champID: String): ResultData<ChampionData> =
        if (!isLoadingGetChampion) {
            try {
                withContext(Dispatchers.IO) {
                    isLoadingGetChampion = true
                    val response = championService.getChampionDetailInfo(VersionManager.getVersion().lvCategory.cvChampion, champID)
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

    private val searchMutex = Mutex()
    suspend fun searchChampion(searchChampionName: String) {
        searchMutex.withLock {
            val searchChampionList = when {
                /* 검색내용이 비어 있을 경우 */
                searchChampionName.isEmpty() -> inMemoryAllChampionList.toList()

                /* 검색 내용이 있을 경우 */
                else -> inMemoryAllChampionList.filter { champion ->
                    /* 검색어 / 검색 대상 공백 제거 */
                    champion.cName.replace(" ", "")
                        .startsWith(searchChampionName.replace(" ", ""))
                }.toList()
            }

            resultAllChampionList.offer(ResultData.Success(searchChampionList))
        }
    }
}