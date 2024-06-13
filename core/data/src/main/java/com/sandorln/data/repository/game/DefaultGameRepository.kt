package com.sandorln.data.repository.game

import com.sandorln.datastore.local.game.GameDatastore
import com.sandorln.network.model.FireStoreGame
import com.sandorln.network.service.GameService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultGameRepository @Inject constructor(
    private val gameDatastore: GameDatastore,
    private val gameService: GameService
) : GameRepository {
    override val initialGameScore: Flow<Long> = gameDatastore.initialGameLocalScore

    override suspend fun getCurrentInitialGameScore(): Long =
        gameService.getCurrentGameScore(FireStoreGame.INITIAL_ITEM)

    override suspend fun updateInitialGameScore(score: Long) {
        gameDatastore.updateInitialGameMaxLocalScore(score)
    }
}