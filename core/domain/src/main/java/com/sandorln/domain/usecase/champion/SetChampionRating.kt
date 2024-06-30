package com.sandorln.domain.usecase.champion

import com.sandorln.data.repository.champion.ChampionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Deprecated("Firebase 사용량 때문에 금지")
@Singleton
class SetChampionRating @Inject constructor(
    private val championRepository: ChampionRepository
) {
    suspend operator fun invoke(championName: String, rating: Int) = runCatching {
        championRepository.setChampionRating(championName, rating)
    }
}