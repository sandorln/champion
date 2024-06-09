package com.sandorln.domain.usecase.game

import com.sandorln.data.repository.game.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetInitialGameScore @Inject constructor(
    private val gameRepository: GameRepository
) {
    suspend operator fun invoke() = runCatching { gameRepository.getCurrentInitialGameScore() }.getOrNull() ?: 0L
}