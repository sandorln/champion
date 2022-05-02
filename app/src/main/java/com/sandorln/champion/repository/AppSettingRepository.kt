package com.sandorln.champion.repository

interface AppSettingRepository {
    suspend fun getAppSettingQuestionNewestLolVersion(): Boolean
    suspend fun toggleAppSettingQuestionNewestLolVersion(): Boolean
}