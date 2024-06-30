package com.sandorln.domain.usecase.champion

import com.sandorln.data.repository.champion.ChampionRepository
import com.sandorln.model.type.ChampionTag
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSimilarChampionList @Inject constructor(
    private val championRepository: ChampionRepository
) {
    suspend operator fun invoke(version: String, tags: List<ChampionTag>) =
        championRepository.getSimilarChampionList(version, tags)
}