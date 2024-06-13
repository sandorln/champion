package com.sandorln.datastore.local.game

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.sandorln.datastore.keys.PreferencesKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultGameDatastore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : GameDatastore {
    companion object {
        private val KEY_INITIAL_GAME_SCORE = longPreferencesKey(PreferencesKeys.INITIAL_GAME_SCORE)
    }

    override val initialGameLocalScore: Flow<Long> = dataStore.data.map { it[KEY_INITIAL_GAME_SCORE] ?: 0L }

    override suspend fun updateInitialGameMaxLocalScore(score: Long) {
        val currentMaxLocalScore = initialGameLocalScore.firstOrNull() ?: 0
        if (currentMaxLocalScore < score)
            dataStore.edit { it[KEY_INITIAL_GAME_SCORE] = score }
    }
}