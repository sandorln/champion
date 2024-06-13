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
        private val KEY_INIT_GAME_SCORE = longPreferencesKey(PreferencesKeys.IS_QUESTION_NEWEST_LOL_VERSION)
    }

    override val maxLocalScore: Flow<Long> = dataStore.data.map { it[KEY_INIT_GAME_SCORE] ?: 0L }

    override suspend fun updateInitGameMaxLocalScore(score: Long) {
        val currentMaxLocalScore = maxLocalScore.firstOrNull() ?: 0
        if (currentMaxLocalScore < score)
            dataStore.edit { it[KEY_INIT_GAME_SCORE] = score }
    }
}