package com.sandorln.data.repository.version

import com.sandorln.model.data.version.Version
import com.sandorln.model.data.version.VersionNewCount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface VersionRepository {
    val currentVersion: Flow<Version>
    val allVersionList: StateFlow<List<Version>>

    suspend fun changeCurrentVersion(versionName: String)

    /**
     * @return 버전의 초기화 목록 다운로드가 완료되지 않은 버전 목록
     */
    suspend fun getNotInitCompleteVersionList(): List<Version>
    suspend fun updateVersionData(version: Version)

    suspend fun getVersionNewCount(versionName: String, preVersionName: String) : VersionNewCount
}