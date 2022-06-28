package com.sandorln.champion.usecase

import com.sandorln.champion.BuildConfig
import com.sandorln.champion.repository.LanguageRepository
import java.util.*
import javax.inject.Inject

class GetLanguageCodeUseCase @Inject constructor(
    private val languageRepository: LanguageRepository
) {
    suspend operator fun invoke(): String =
        try {
            val deviceLanguage = Locale.getDefault().language
            val deviceCountry = Locale.getDefault().country
            val deviceLanguageCode = "${deviceLanguage}_${deviceCountry}"

            val lolLanguage = languageRepository.getLanguages()

            if (lolLanguage.contains(deviceLanguageCode)) {
                deviceLanguageCode
            } else {
                lolLanguage.firstOrNull { it.startsWith(deviceLanguage) } ?: BuildConfig.DEFAULT_LANGUAGE
            }
        } catch (e: Exception) {
            BuildConfig.DEFAULT_LANGUAGE
        }
}