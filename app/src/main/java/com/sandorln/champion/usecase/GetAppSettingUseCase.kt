package com.sandorln.champion.usecase

import com.sandorln.champion.model.type.AppSettingType
import com.sandorln.champion.repository.AppSettingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetAppSettingUseCase @Inject constructor(
    private val appSettingRepository: AppSettingRepository
) {
    suspend operator fun invoke(appSettingType: AppSettingType): Boolean = withContext(Dispatchers.IO) {
        when (appSettingType) {
            AppSettingType.QUESTION_NEWEST_LOL_VERSION -> appSettingRepository.getAppSettingQuestionNewestLolVersion()
            AppSettingType.VIDEO_WIFI_MODE_AUTO_PLAY -> appSettingRepository.getAppSettingVideoWifiModeAutoPlay()
        }
    }
}