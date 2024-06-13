package com.sandorln.datastore.local.version

import kotlinx.coroutines.flow.Flow

interface VersionDatasource {
    val currentVersion: Flow<String>

    suspend fun changeCurrentVersion(version: String)
}