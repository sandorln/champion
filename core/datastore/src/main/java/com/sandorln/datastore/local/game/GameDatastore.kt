package com.sandorln.datastore.local.game

import kotlinx.coroutines.flow.Flow

interface GameDatastore {
    val initialGameLocalScore: Flow<Long>
    val refreshRankGameNextTime: Flow<Long>

    suspend fun updateInitialGameMaxLocalScore(score: Long)
    suspend fun updateRefreshRankGameNextTime()
}