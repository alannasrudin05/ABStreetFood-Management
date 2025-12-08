package com.praktikum.abstreetfood_management.data.repository

import com.praktikum.abstreetfood_management.data.local.entity.UserEntity
import com.praktikum.abstreetfood_management.data.remote.response.UserDto

/**
* IUserRepository: Interface Domain untuk Repository.
* Mendefinisikan kontrak data yang harus dipenuhi oleh implementasi (UserRepository).
*/
interface IUserRepository {
    // Fungsi Login: mencari user di remote (Turso)
    suspend fun loginRemote(email: String, password: String): UserEntity?

    // Fungsi Login: mencari user di local (Room)
    suspend fun loginLocal(email: String, password: String): UserEntity?
    suspend fun getUserLocal(email: String): UserEntity?
    suspend fun registerRemote(name: String, email: String, password: String): UserDto?

    // BARU: Fungsi Register: mendaftarkan user di remote (Turso)
//    suspend fun registerRemote(user: UserEntity): UserEntity? // <-- BARU

    // BARU: Fungsi Register: mendaftarkan user di local (Room)
//    suspend fun registerLocal(user: UserEntity): UserEntity? // <-- BARU

    // Fungsi untuk menyimpan user hasil login ke database local
    suspend fun saveUserLocal(user: UserEntity)

    // Fungsi untuk mendapatkan semua user (dengan opsi force refresh)
//    suspend fun getUsers(forceRefresh: Boolean = false): List<UserEntity>

    // Fungsi untuk sinkronisasi data dari remote ke local di latar belakang
//    suspend fun syncIfNeeded()

    /** Memperbarui detail pengguna di lokal dan remote (jika ada) */
//    suspend fun updateUser(user: UserEntity): Result<Unit> // <-- BARU

    /** Menghapus pengguna dari lokal dan remote (jika ada) */
//    suspend fun deleteUser(userId: String): Result<Unit> // <-- BARU
}