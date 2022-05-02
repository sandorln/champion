package com.sandorln.champion.repository

interface AppSettingRepository {
    suspend fun getAppSettingQuestionNewestLolVersion(): Boolean
    suspend fun toggleAppSettingQuestionNewestLolVersion(): Boolean
    suspend fun getAppSettingVideoWifiModeAutoPlay(): Boolean
    suspend fun toggleAppSettingVideoWifiModeOnlyPlay(): Boolean
}