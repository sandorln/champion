package com.sandorln.champion.database.shareddao

import android.content.SharedPreferences
import androidx.core.content.edit

class AppSettingDaoImpl(
    private val pref: SharedPreferences
) : AppSettingDao {
    companion object {
        private const val KEY_SETTING_QUESTION_NEWEST_LOL_VERSION = "key_setting_question_newest_lol_version"
    }

    override fun settingQuestionNewestLolVersion(): Boolean = pref.getBoolean(KEY_SETTING_QUESTION_NEWEST_LOL_VERSION, true)
    override fun toggleAppSettingQuestionNewestLolVersion(): Boolean {
        val toggleValue = !pref.getBoolean(KEY_SETTING_QUESTION_NEWEST_LOL_VERSION, true)
        pref.edit(commit = true) { putBoolean(KEY_SETTING_QUESTION_NEWEST_LOL_VERSION, toggleValue) }
        return toggleValue
    }
}