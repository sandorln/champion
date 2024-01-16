package com.sandorln.champion.usecase

import com.sandorln.model.type.AppSettingType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAppSettingUseCase @Inject constructor() {
    suspend operator fun invoke(appSettingType: AppSettingType): Boolean = withContext(Dispatchers.IO) {
        when (appSettingType) {
            AppSettingType.QUESTION_NEWEST_LOL_VERSION -> false
            AppSettingType.VIDEO_WIFI_MODE_AUTO_PLAY -> false
        }
    }
}