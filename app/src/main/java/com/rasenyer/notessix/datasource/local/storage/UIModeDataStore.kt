package com.rasenyer.notessix.datasource.local.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.createDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

abstract class PrefsDataStore(context: Context, fileName: String) {
    internal val dataStore: DataStore<Preferences> = context.createDataStore(fileName)
}

class UIModeDataStore(context: Context) : PrefsDataStore(context, PREF_FILE_UI_MODE), UIModeImpl {

    override val getUIMode: Flow<Boolean>
        get() = dataStore.data.map { preferences ->
            val uiMode = preferences[UI_MODE_KEY] ?: false
            uiMode
        }

    override suspend fun saveUIModeToDataStore(isNightMode: Boolean) {
        dataStore.edit { preferences ->
            preferences[UI_MODE_KEY] = isNightMode
        }
    }

    companion object {
        private const val PREF_FILE_UI_MODE = "ui_mode_preference"
        private val UI_MODE_KEY = booleanPreferencesKey("ui_mode")
    }

}

@Singleton
interface UIModeImpl {

    val getUIMode: Flow<Boolean>
    suspend fun saveUIModeToDataStore(isNightMode: Boolean)

}