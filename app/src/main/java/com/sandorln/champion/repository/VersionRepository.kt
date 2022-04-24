package com.sandorln.champion.repository

import com.sandorln.champion.model.VersionLol.VersionCategory
import kotlinx.coroutines.flow.Flow

interface VersionRepository {
    @Deprecated("더이상 각 카테고리 별 버전을 사용하지 않음")
    fun getLolVersionCategory(): Flow<VersionCategory>

    suspend fun getLolVersion(): String
    suspend fun changeLolVersion(version: String)
    suspend fun getLolVersionList(): List<String>

    /**
     * Total Version 과 Champion Version 이 다른 경우가 있어서
     * 해당 Total Version 에 맞는 Champion Version 을 불러오기
     */
    suspend fun getLolChampionVersion(totalVersion: String): String
}