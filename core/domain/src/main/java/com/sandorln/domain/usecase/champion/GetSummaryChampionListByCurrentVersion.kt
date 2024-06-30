package com.sandorln.domain.usecase.champion

import com.sandorln.data.repository.champion.ChampionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSummaryChampionListByCurrentVersion @Inject constructor(
    private val championRepository: ChampionRepository
) {
    operator fun invoke() = championRepository.currentSummaryChampionList
}