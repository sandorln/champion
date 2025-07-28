package com.sandorln.domain.usecase.rune

import com.sandorln.data.repository.rune.RuneRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetRuneStyleListByCurrentVersion @Inject constructor(
    private val runeRepository: RuneRepository
) {
    operator fun invoke() = runeRepository.currentRuneStyleList
}