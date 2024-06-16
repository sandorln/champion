package com.sandorln.data.repository.game

import com.sandorln.model.type.GameType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface GameRepository {
    val gameRank: MutableStateFlow<Map<GameType, Int>>
    val refreshRankGameNextTime : Flow<Long>

    val initialGameScore: Flow<Long>

    suspend fun getCurrentInitialGameScore(): Long
    suspend fun updateInitialGameScore(score: Long)
    suspend fun refreshGameRank()
}