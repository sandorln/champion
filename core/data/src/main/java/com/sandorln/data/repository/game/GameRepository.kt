package com.sandorln.data.repository.game

import kotlinx.coroutines.flow.Flow

interface GameRepository {
    val initialGameScore: Flow<Long>

    suspend fun getCurrentInitialGameScore(): Long
    suspend fun updateInitialGameScore(score: Long)
}