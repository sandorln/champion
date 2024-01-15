package com.sandorln.champion.usecase

import com.sandorln.model.type.AppSettingType
import com.sandorln.champion.repository.AppSettingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ToggleAppSettingUseCase @Inject constructor(
    private val appSettingRepository: AppSettingRepository
) {
    suspend operator fun invoke(appSettingType: com.sandorln.model.type.AppSettingType) = withContext(Dispatchers.IO) {
        when (appSettingType) {
            com.sandorln.model.type.AppSettingType.QUESTION_NEWEST_LOL_VERSION -> appSettingRepository.toggleAppSettingQuestionNewestLolVersion()
            com.sandorln.model.type.AppSettingType.VIDEO_WIFI_MODE_AUTO_PLAY -> appSettingRepository.toggleAppSettingVideoWifiModeOnlyPlay()
        }
    }
}