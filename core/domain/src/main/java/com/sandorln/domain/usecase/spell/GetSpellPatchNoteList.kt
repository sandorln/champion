package com.sandorln.domain.usecase.spell

import com.sandorln.data.repository.spell.SummonerSpellRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSpellPatchNoteList @Inject constructor(
    private val summonerSpellRepository: SummonerSpellRepository
) {
    suspend operator fun invoke(version: String) = runCatching {
        summonerSpellRepository.getSpellPatchList(version)
    }
}
