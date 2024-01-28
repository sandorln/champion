package com.sandorln.data.repository.version

import com.sandorln.model.data.version.Version
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface VersionRepository {
    val currentVersion: Flow<Version>
    val allVersionList: StateFlow<List<Version>>

    suspend fun changeCurrentVersion(versionName: String)

    suspend fun refreshVersionList(): Result<Any>
    suspend fun getNotInitCompleteVersionList(): List<Version>
    suspend fun updateVersionData(version: Version)
}