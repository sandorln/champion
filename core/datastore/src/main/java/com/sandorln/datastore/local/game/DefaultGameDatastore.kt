package com.sandorln.datastore.local.game

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.sandorln.datastore.keys.PreferencesKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

class DefaultGameDatastore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : GameDatastore {
    companion object {
        private const val REFRESH_DELAY_MINUTE_TIME = 1

        private val KEY_INITIAL_GAME_SCORE = longPreferencesKey(PreferencesKeys.INITIAL_GAME_SCORE)
        private val KEY_REFRESH_RANK_GAME_NEXT_TIME = longPreferencesKey(PreferencesKeys.REFRESH_RANK_GAME_NEXT_TIME)
    }

    override val initialGameLocalScore: Flow<Long> = dataStore.data.map { it[KEY_INITIAL_GAME_SCORE] ?: 0L }
    override val refreshRankGameNextTime: Flow<Long> = dataStore.data.map { it[KEY_REFRESH_RANK_GAME_NEXT_TIME] ?: 0L }

    override suspend fun updateInitialGameMaxLocalScore(score: Long) {
        val currentMaxLocalScore = initialGameLocalScore.firstOrNull() ?: 0
        if (currentMaxLocalScore < score)
            dataStore.edit { it[KEY_INITIAL_GAME_SCORE] = score }
    }

    override suspend fun updateRefreshRankGameNextTime() {
        val nextRefreshLocalTime = Calendar
            .getInstance(TimeZone.getTimeZone("UTC")).apply {
                add(Calendar.MINUTE, REFRESH_DELAY_MINUTE_TIME)
            }.timeInMillis

        dataStore.edit {
            it[KEY_REFRESH_RANK_GAME_NEXT_TIME] = nextRefreshLocalTime
        }
    }
}