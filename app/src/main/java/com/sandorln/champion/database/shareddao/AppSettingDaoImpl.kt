package com.sandorln.champion.database.shareddao

import android.content.SharedPreferences
import androidx.core.content.edit

class AppSettingDaoImpl(
    private val pref: SharedPreferences
) : AppSettingDao {
    companion object {
        private const val KEY_SETTING_QUESTION_NEWEST_LOL_VERSION = "key_setting_question_newest_lol_version"
        private const val KEY_SETTING_VIDEO_WIFI_MODE_AUTO_PLAY = "key_setting_video_wifi_mode_auto_play"
    }

    override fun settingQuestionNewestLolVersion(): Boolean = pref.getBoolean(KEY_SETTING_QUESTION_NEWEST_LOL_VERSION, true)
    override fun toggleAppSettingQuestionNewestLolVersion(): Boolean = toggleAppSetting(KEY_SETTING_QUESTION_NEWEST_LOL_VERSION)

    override fun settingVideoWifiModeAutoPlay(): Boolean = pref.getBoolean(KEY_SETTING_VIDEO_WIFI_MODE_AUTO_PLAY, true)
    override fun toggleAppSettingVideoWifiModeAutoPlay(): Boolean = toggleAppSetting(KEY_SETTING_VIDEO_WIFI_MODE_AUTO_PLAY)

    private fun toggleAppSetting(key: String, defaultValue: Boolean = true): Boolean {
        val toggleValue = !pref.getBoolean(key, defaultValue)
        pref.edit(commit = true) { putBoolean(key, toggleValue) }
        return toggleValue
    }
}