package com.sandorln.champion.repository

import com.sandorln.champion.manager.VersionManager
import com.sandorln.champion.model.CharacterData
import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.network.ChampionService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext

@FlowPreview
@ExperimentalCoroutinesApi
class ChampionRepository(
    private val championService: ChampionService,
    private val versionManager: VersionManager
) {

    /**
     * 모든 챔피언 정보 가져오기
     */
    private val inMemoryAllChampionList = mutableListOf<CharacterData>()
    private var isLoadingAllChampion = false
    private val resultAllChampionList = ConflatedBroadcastChannel<ResultData<List<CharacterData>>>()
    suspend fun getResultAllChampionList(): Flow<ResultData<List<CharacterData>>> {
        inMemoryAllChampionList.clear()
        isLoadingAllChampion = false
        requestAllChampion()
        return resultAllChampionList.asFlow()
    }

    private suspend fun requestAllChampion() {
        if (!isLoadingAllChampion) {
            try {
                isLoadingAllChampion = true
                val response = championService.getAllChampion(versionManager.getVersion().lvCategory.cvChampion)
                response.parsingData()
                inMemoryAllChampionList.addAll(response.rCharacterList.sortedBy { it.cName })
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
    suspend fun getChampionInfo(champID: String): ResultData<CharacterData> =
        if (!isLoadingGetChampion) {
            try {
                withContext(Dispatchers.IO) {
                    isLoadingGetChampion = true
                    val response = championService.getChampionDetailInfo(versionManager.getVersion().lvCategory.cvChampion, champID)
                    response.parsingData()
                    ResultData.Success(response.rCharacterList.first())
                }
            } catch (e: Exception) {
                ResultData.Failed(e)
            } finally {
                isLoadingGetChampion = false
            }
        } else
            ResultData.Failed(Exception("이미 로딩중 입니다"))
}