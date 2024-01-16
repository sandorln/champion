package com.sandorln.data.repository.version

import kotlinx.coroutines.flow.Flow

interface VersionRepository {
    val currentVersion: Flow<String>
    val allVersionList: Flow<List<String>>

    suspend fun changeCurrentVersion(version: String)
    suspend fun refreshVersionList()
}