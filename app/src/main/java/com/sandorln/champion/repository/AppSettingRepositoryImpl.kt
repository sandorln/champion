package com.sandorln.champion.repository

import com.sandorln.champion.database.shareddao.AppSettingDao
import javax.inject.Inject

class AppSettingRepositoryImpl @Inject constructor(
    private val appSettingDao: AppSettingDao
) : AppSettingRepository {
    override suspend fun getAppSettingQuestionNewestLolVersion(): Boolean = appSettingDao.settingQuestionNewestLolVersion()
    override suspend fun toggleAppSettingQuestionNewestLolVersion(): Boolean = appSettingDao.toggleAppSettingQuestionNewestLolVersion()
}