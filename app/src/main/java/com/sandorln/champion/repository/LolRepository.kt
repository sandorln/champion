package com.sandorln.champion.repository

import com.sandorln.champion.model.CharacterData
import com.sandorln.champion.model.LolVersion
import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.network.LolApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext

@FlowPreview
@ExperimentalCoroutinesApi
class LolRepository {

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

    suspend fun requestAllChampion() {
        if (!isLoadingAllChampion) {
            try {
                isLoadingAllChampion = true
                val response = LolApiClient.getService().getAllChampion(LolApiClient.lolVersion!!.lvCategory.cvChampion)
                inMemoryAllChampionList.addAll(response.rCharacterList)
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
                    val response = LolApiClient.getService().getChampionDetailInfo(LolApiClient.lolVersion!!.lvCategory.cvChampion, champID)
                    ResultData.Success(response.rCharacterList.first())
                }
            } catch (e: Exception) {
                ResultData.Failed(e)
            } finally {
                isLoadingGetChampion = false
            }
        } else
            ResultData.Failed(Exception("이미 로딩중 입니다"))

    /**
     * 버전 값 가져오기
     */
    suspend fun getVersion(): ResultData<LolVersion> =
        try {
            LolApiClient.lolVersion = LolApiClient.getService().getVersion()
            ResultData.Success(LolApiClient.lolVersion)
        } catch (e: Exception) {
            ResultData.Failed(e)
        }
}