package com.sandorln.datastore.local.version

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.sandorln.datastore.keys.PreferencesKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultVersionDatasource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : VersionDatasource {
    companion object {
        private val KEY_VERSION = stringPreferencesKey(PreferencesKeys.VERSION)
    }

    override val currentVersion: Flow<String> = dataStore.data.map { it[KEY_VERSION] ?: "" }

    override suspend fun changeCurrentVersion(version: String) {
        dataStore.edit { it[KEY_VERSION] = version }
    }
}