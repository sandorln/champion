package com.sandorln.data.repository.game

import com.sandorln.network.model.FireStoreGame
import com.sandorln.network.service.GameService
import javax.inject.Inject

class DefaultGameRepository @Inject constructor(
    private val gameService: GameService
) : GameRepository {
    override suspend fun getCurrentInitialGameScore(): Long =
        gameService.getCurrentGameScore(FireStoreGame.INITIAL_ITEM)

    override suspend fun updateInitialGameScore(score: Long) {
        gameService.updateGameScore(FireStoreGame.INITIAL_ITEM, score)
    }
}