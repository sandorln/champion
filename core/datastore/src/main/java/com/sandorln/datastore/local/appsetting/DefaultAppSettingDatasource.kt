package com.sandorln.datastore.local.appsetting

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.sandorln.datastore.keys.PreferencesKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultAppSettingDatasource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : AppSettingDatasource {
    companion object {
        private val KEY_IS_QUESTION_NEWEST_VERSION = booleanPreferencesKey(PreferencesKeys.IS_QUESTION_NEWEST_LOL_VERSION)
        private val KEY_IS_VIDEO_WIFI_MODE_AUTO_PLAY = booleanPreferencesKey(PreferencesKeys.IS_VIDEO_WIFI_MODE_AUTO_PLAY)
    }

    override val isQuestionNewestLolVersion: Flow<Boolean> = dataStore.data.map { it[KEY_IS_QUESTION_NEWEST_VERSION] ?: false }
    override val isAutoPlayOnlyWifiMode: Flow<Boolean> = dataStore.data.map { it[KEY_IS_VIDEO_WIFI_MODE_AUTO_PLAY] ?: false }

    override suspend fun toggleIsQuestionNewestLolVersion() {
        val oldValue = isQuestionNewestLolVersion.firstOrNull() ?: false
        dataStore.edit { it[KEY_IS_QUESTION_NEWEST_VERSION] = !oldValue }
    }

    override suspend fun toggleIsAutoPlayOnlyWifiMode() {
        val oldValue = isAutoPlayOnlyWifiMode.firstOrNull() ?: false
        dataStore.edit { it[KEY_IS_VIDEO_WIFI_MODE_AUTO_PLAY] = !oldValue }
    }
}