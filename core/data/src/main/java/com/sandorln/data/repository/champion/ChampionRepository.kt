package com.sandorln.data.repository.champion

import com.sandorln.model.data.champion.ChampionDetailData
import com.sandorln.model.data.champion.ChampionPatchNote
import com.sandorln.model.data.champion.SummaryChampion
import com.sandorln.model.type.ChampionTag
import kotlinx.coroutines.flow.Flow

interface ChampionRepository {
    val currentSummaryChampionList: Flow<List<SummaryChampion>>

    suspend fun refreshChampionList(version: String): Result<Any>

    suspend fun getSummaryChampionListByVersion(version: String): List<SummaryChampion>
    suspend fun getSummaryChampionListByChampionIdList(version: String, championIdList: List<String>): List<SummaryChampion>

    suspend fun getNewChampionIdList(versionName: String, preVersionName: String): List<String>

    suspend fun getSummaryChampion(championId: String, version: String): SummaryChampion?
    suspend fun getChampionDetail(championId: String, version: String): ChampionDetailData
    suspend fun hasChampionDetailData(championId: String, version: String): Boolean
    suspend fun getSimilarChampionList(version: String, tags: List<ChampionTag>): List<SummaryChampion>

    suspend fun getChampionVersionList(championId: String): List<String>
    suspend fun getChampionDiffStatusVersion(championId: String): Map<String, Boolean>
    suspend fun getChampionPatchNoteList(version: String): List<ChampionPatchNote>

    /**
     * @return 변경 된 후 평점 값 (Total / UserRating)
     */
    @Deprecated("Firebase 사용량 때문에 금지")
    suspend fun setChampionRating(championName: String, rating : Int) : Pair<Float,Int>
}