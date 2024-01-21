package com.sandorln.data.repository.version

import com.sandorln.model.data.version.Version
import kotlinx.coroutines.flow.Flow

interface VersionRepository {
    val currentVersion: Flow<String>
    val allVersionList: Flow<List<Version>>

    suspend fun changeCurrentVersion(versionName: String)
    suspend fun refreshVersionList()

    /**
     * @return 버전의 초기화(목록/스프라이트 다운로드)가 완료되지 않은 버전 목록
     */
    suspend fun getNotInitCompleteVersionList(): List<Version>
    suspend fun updateVersionData(version: Version)
}