package com.praktikum.abstreetfood_management.data.repository

import kotlinx.coroutines.flow.Flow

/**
 * Interface untuk menyimpan dan mengambil Token JWT.
 * Implementasi sebenarnya menggunakan DataStore/SharedPreferences.
 */
interface IAuthPreferences {
    suspend fun saveAuthToken(token: String)
    fun getAuthToken(): Flow<String?>
    suspend fun clearAuthToken()
    suspend fun saveUserSession(userId: String, role: String, name: String)
    fun getUserId(): Flow<String?> // Mengambil ID pengguna
    fun getUserRole(): Flow<String?>
    fun getUserName(): Flow<String?>
}