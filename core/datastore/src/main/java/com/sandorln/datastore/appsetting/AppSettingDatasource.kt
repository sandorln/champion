package com.sandorln.datastore.appsetting

import kotlinx.coroutines.flow.Flow

interface AppSettingDatasource {
    val isQuestionNewestLolVersion: Flow<Boolean>
    val isAutoPlayOnlyWifiMode: Flow<Boolean>

    suspend fun toggleIsQuestionNewestLolVersion()
    suspend fun toggleIsAutoPlayOnlyWifiMode()
}