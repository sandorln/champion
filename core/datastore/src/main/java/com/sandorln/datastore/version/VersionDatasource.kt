package com.sandorln.datastore.version

import androidx.datastore.preferences.core.booleanPreferencesKey
import kotlinx.coroutines.flow.Flow

interface VersionDatasource {
    companion object {
        private val KEY_IS_QUESTION_NEWEST_VERSION = booleanPreferencesKey("key_setting_question_newest_lol_version")
        private val KEY_IS_VIDEO_WIFI_MODE_AUTO_PLAY = booleanPreferencesKey("key_setting_video_wifi_mode_auto_play")
    }

    val currentVersion: Flow<String>

    suspend fun changeCurrentVersion(version: String)
}