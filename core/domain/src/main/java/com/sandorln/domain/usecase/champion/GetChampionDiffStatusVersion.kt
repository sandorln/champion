package com.sandorln.domain.usecase.champion

import com.sandorln.data.repository.champion.ChampionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetChampionDiffStatusVersion @Inject constructor(
    private val championRepository: ChampionRepository
) {
    suspend operator fun invoke(championId: String) =
        championRepository.getChampionDiffStatusVersion(championId)
}