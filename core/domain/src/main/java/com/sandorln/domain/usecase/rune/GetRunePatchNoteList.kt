package com.sandorln.domain.usecase.rune

import com.sandorln.data.repository.rune.RuneRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetRunePatchNoteList @Inject constructor(
    private val runeRepository: RuneRepository
) {
    suspend operator fun invoke(version: String) = runCatching {
        runeRepository.getRunePatchList(version)
    }
}