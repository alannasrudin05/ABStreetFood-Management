package com.praktikum.abstreetfood_management.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.praktikum.abstreetfood_management.data.local.entity.UserEntity

@Dao
interface UserDao {
    // Login: Cari user berdasarkan email dan password
    @Query("SELECT * FROM users WHERE email = :email AND password = :password AND isActive = 1 LIMIT 1")
    suspend fun getUserByCredentials(email: String, password: String): UserEntity?

    // Cek apakah email sudah terdaftar
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    // Get all users
    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserEntity>

    // Get user by ID
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    // Insert single user
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // Insert multiple users
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    // Update user
    @Update
    suspend fun updateUser(user: UserEntity)

    // Update last synced time
    @Query("UPDATE users SET lastSyncedAt = :timestamp WHERE id = :userId")
    suspend fun updateLastSynced(userId: String, timestamp: Long)

    // Clear all users
    @Query("DELETE FROM users")
    suspend fun clearAll()

    // Get users that need sync (older than X time)
    @Query("SELECT * FROM users WHERE lastSyncedAt < :timestamp")
    suspend fun getUsersNeedingSync(timestamp: Long): List<UserEntity>
}