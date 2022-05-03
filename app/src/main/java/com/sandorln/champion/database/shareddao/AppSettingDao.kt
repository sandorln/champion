package com.sandorln.champion.database.shareddao

interface AppSettingDao {
    /* Splash 화면에서 최신 버전 묻기 여부 */
    fun settingQuestionNewestLolVersion(): Boolean
    fun toggleAppSettingQuestionNewestLolVersion(): Boolean

    fun settingVideoWifiModeAutoPlay(): Boolean
    fun toggleAppSettingVideoWifiModeAutoPlay(): Boolean
}