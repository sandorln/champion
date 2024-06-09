package com.sandorln.domain.usecase.game

import com.sandorln.data.repository.game.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateInitialGameScore @Inject constructor(
    private val gameRepository: GameRepository
) {
    suspend operator fun invoke(score: Long) = runCatching {
        val preScore = gameRepository.getCurrentInitialGameScore()
        if (score > preScore)
            gameRepository.updateInitialGameScore(score)
    }
}