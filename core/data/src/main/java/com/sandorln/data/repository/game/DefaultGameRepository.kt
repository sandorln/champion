package com.sandorln.data.repository.game

import com.sandorln.datastore.local.game.GameDatastore
import com.sandorln.model.type.GameType
import com.sandorln.network.model.FireStoreGame
import com.sandorln.network.service.GameService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class DefaultGameRepository @Inject constructor(
    private val gameDatastore: GameDatastore,
    private val gameService: GameService
) : GameRepository {
    override val gameRank: MutableStateFlow<Map<GameType, Int>> = MutableStateFlow(emptyMap())
    override val refreshRankGameNextTime: Flow<Long> = gameDatastore.refreshRankGameNextTime

    override val initialGameScore: Flow<Long> = gameDatastore.initialGameLocalScore

    override suspend fun updateInitialGameScore(score: Long) {
        gameDatastore.updateInitialGameMaxLocalScore(score)
    }

    override suspend fun refreshGameRank() {
        val currentScore = initialGameScore.first()
        gameService.updateGameScore(FireStoreGame.INITIAL_ITEM, currentScore)

        val rank = gameService.getInitialGameRank(FireStoreGame.INITIAL_ITEM, currentScore)

        gameRank.update {
            mapOf(GameType.Initial to rank)
        }

        gameDatastore.updateRefreshRankGameNextTime()
    }
}