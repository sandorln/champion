package com.sandorln.domain.usecase.spell

import com.sandorln.data.repository.spell.SummonerSpellRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSpellListByCurrentVersion @Inject constructor(
    private val spellRepository: SummonerSpellRepository
) {
    operator fun invoke() = spellRepository.currentItemList
}