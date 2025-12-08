package com.praktikum.abstreetfood_management.data.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

// Ganti dengan implementasi SharedPreferences/DataStore yang sebenarnya
@Singleton
class AuthPreferencesImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : IAuthPreferences {

    // --- CONTOH IMPLEMENTASI SEMENTARA ---
    private val sharedPrefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    private val TOKEN_KEY = "auth_token"
    private val USER_ID_KEY = "user_id" // Kunci baru untuk menyimpan User ID
    private val USER_ROLE_KEY = "user_role" // Kunci baru untuk menyimpan Role
    private val USER_NAME_KEY = "user_name" // Kunci baru untuk menyimpan Role


    // =======================================================
    // 1. TOKEN HANDLING
    // =======================================================

    override suspend fun saveAuthToken(token: String) {
        sharedPrefs.edit().putString(TOKEN_KEY, token).apply()
    }

    override fun getAuthToken(): Flow<String?> {
        // Karena SharedPreferences bukan Flow, ini hanya contoh sederhana.
        // Sebaiknya gunakan DataStore di implementasi nyata.
        return flowOf(sharedPrefs.getString(TOKEN_KEY, null))
    }

    // =======================================================
    // 2. USER SESSION HANDLING (BARU)
    // =======================================================

    /**
     * Menyimpan User ID dan Role setelah login sukses.
     */
    override suspend fun saveUserSession(userId: String, role: String, name: String) {
        sharedPrefs.edit()
            .putString(USER_ID_KEY, userId)
            .putString(USER_ROLE_KEY, role)
            .putString(USER_NAME_KEY, name)
            .apply()
    }

    /**
     * Mengambil User ID (digunakan untuk transaksi, filter data, dll.)
     */
    override fun getUserId(): Flow<String?> {
        return flowOf(sharedPrefs.getString(USER_ID_KEY, null))
    }

    /**
     * Mengambil Role pengguna (digunakan untuk otorisasi/hak akses).
     */
    override fun getUserRole(): Flow<String?> {
        return flowOf(sharedPrefs.getString(USER_ROLE_KEY, null))
    }

    override fun getUserName(): Flow<String?> {
        return flowOf(sharedPrefs.getString(USER_NAME_KEY, null))
    }

    override suspend fun clearAuthToken() {
        sharedPrefs.edit().remove(TOKEN_KEY).apply()
    }
}