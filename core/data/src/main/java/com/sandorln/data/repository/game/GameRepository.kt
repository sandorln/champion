package com.sandorln.data.repository.game

interface GameRepository {
    suspend fun getCurrentInitialGameScore(): Long
    suspend fun updateInitialGameScore(score: Long)
}