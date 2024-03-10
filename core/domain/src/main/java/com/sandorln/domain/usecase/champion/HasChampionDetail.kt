package com.sandorln.domain.usecase.champion

import com.sandorln.data.repository.champion.ChampionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HasChampionDetail @Inject constructor(
    private val championRepository: ChampionRepository
) {
    suspend operator fun invoke(championId: String, version: String) = runCatching {
        championRepository.hasChampionDetailData(championId, version)
    }
}