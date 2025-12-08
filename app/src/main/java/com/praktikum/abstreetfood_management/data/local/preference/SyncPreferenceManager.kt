package com.praktikum.abstreetfood_management.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Pastikan Anda sudah menambahkan dependency DataStore
// val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SyncPreferenceManager(private val context: Context) {

    // Key untuk menyimpan status sync
    private val IS_SYNC_ENABLED = booleanPreferencesKey("is_sync_enabled")

    // Asumsi Anda sudah menginisialisasi DataStore
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")
    private val dataStore = context.dataStore

    // Mengambil status sync (berbentuk Flow)
    val isSyncEnabledFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            // Nilai default adalah TRUE (sync aktif)
            preferences[IS_SYNC_ENABLED] ?: true
        }

    // Mengubah status sync
    suspend fun setSyncEnabled(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_SYNC_ENABLED] = isEnabled
        }
    }
}