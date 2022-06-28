package com.sandorln.champion.repository

import com.sandorln.champion.database.shareddao.LanguageDao
import com.sandorln.champion.network.LanguageService
import javax.inject.Inject

class LanguageRepositoryImpl @Inject constructor(
    private val languageService: LanguageService,
    private val languageDao: LanguageDao
) : LanguageRepository {
    private var languages: List<String> = mutableListOf()
    override suspend fun getLanguages(): List<String> =
        try {
            languages.ifEmpty {
                var localDbLanguages = languageDao.getLanguages()
                localDbLanguages = localDbLanguages.ifEmpty {
                    val serverLanguages = languageService.getLanguages()
                    languageDao.insertLanguages(serverLanguages)
                    serverLanguages
                }

                languages = localDbLanguages
            }

            languages
        } catch (e: Exception) {
            mutableListOf()
        }
}