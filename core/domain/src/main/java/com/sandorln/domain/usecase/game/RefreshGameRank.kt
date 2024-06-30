package com.sandorln.domain.usecase.game

import com.sandorln.data.repository.game.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefreshGameRank @Inject constructor(
    private val gameRepository: GameRepository
) {
    suspend operator fun invoke() = runCatching {
        gameRepository.refreshGameRank()
    }
}