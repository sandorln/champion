package com.sandorln.domain.usecase.champion

import com.sandorln.data.repository.champion.ChampionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetChampionRating @Inject constructor(
    private val championRepository: ChampionRepository
) {
    suspend operator fun invoke(championName: String, rating: Int) = runCatching {
        championRepository.setChampionRating(championName, rating)
    }
}