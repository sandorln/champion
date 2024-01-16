package com.sandorln.champion.usecase

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetLanguageCodeUseCase @Inject constructor() {
    suspend operator fun invoke(): String =
        try {
            ""
        } catch (e: Exception) {
            ""
        }
}