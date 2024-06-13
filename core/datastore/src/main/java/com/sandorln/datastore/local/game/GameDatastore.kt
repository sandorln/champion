package com.sandorln.datastore.local.game

import kotlinx.coroutines.flow.Flow

interface GameDatastore {
    val maxLocalScore: Flow<Long>

    suspend fun updateInitGameMaxLocalScore(score : Long)
}