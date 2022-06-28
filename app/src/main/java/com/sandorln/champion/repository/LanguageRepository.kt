package com.sandorln.champion.repository

interface LanguageRepository {
    suspend fun getLanguages(): List<String>
}