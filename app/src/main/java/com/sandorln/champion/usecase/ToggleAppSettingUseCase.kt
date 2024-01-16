package com.sandorln.champion.usecase

import com.sandorln.model.type.AppSettingType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ToggleAppSettingUseCase @Inject constructor() {
    suspend operator fun invoke(appSettingType: AppSettingType) = withContext(Dispatchers.IO) {
    }
}