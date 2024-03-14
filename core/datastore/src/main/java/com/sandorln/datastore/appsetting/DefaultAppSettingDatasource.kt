package com.sandorln.datastore.appsetting

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultAppSettingDatasource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : AppSettingDatasource {
    companion object {
        private val KEY_IS_QUESTION_NEWEST_VERSION = booleanPreferencesKey("key_setting_question_newest_lol_version")
        private val KEY_IS_VIDEO_WIFI_MODE_AUTO_PLAY = booleanPreferencesKey("key_setting_video_wifi_mode_auto_play")
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