package com.sandorln.champion.repository

import com.sandorln.model.VersionLol.VersionCategory
import kotlinx.coroutines.flow.Flow

interface VersionRepository {
    @Deprecated("더이상 각 카테고리 별 버전을 사용하지 않음")
    fun getLolVersionCategory(): Flow<VersionCategory>

    suspend fun getLolVersion(): String
    suspend fun changeLolVersion(version: String)
    suspend fun getLolVersionList(): List<String>

    /* 현재 설정되어 있는 전체 버전로 해당 버전 값을 자동으로 가져오기 */
    suspend fun getLolChampionVersion(): String
    suspend fun getLolItemVersion(): String
    suspend fun getLolSummonerSpellVersion(): String

    /* 특정 전체 버전에서의 해당 버전 값을 가져오기 */
    suspend fun getLolChampionVersionByVersionName(version: String): String
}