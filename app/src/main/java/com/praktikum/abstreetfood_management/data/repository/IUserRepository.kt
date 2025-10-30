package com.praktikum.abstreetfood_management.data.repository

import com.praktikum.abstreetfood_management.data.local.entity.UserEntity

/**
* IUserRepository: Interface Domain untuk Repository.
* Mendefinisikan kontrak data yang harus dipenuhi oleh implementasi (UserRepository).
*/
interface IUserRepository {
    // Fungsi Login: mencari user di remote (Turso)
    suspend fun loginRemote(email: String, password: String): UserEntity?

    // Fungsi Login: mencari user di local (Room)
    suspend fun loginLocal(email: String, password: String): UserEntity?

    // Fungsi untuk menyimpan user hasil login ke database local
    suspend fun saveUserLocal(user: UserEntity)

    // Fungsi untuk mendapatkan semua user (dengan opsi force refresh)
    suspend fun getUsers(forceRefresh: Boolean = false): List<UserEntity>

    // Fungsi untuk sinkronisasi data dari remote ke local di latar belakang
    suspend fun syncIfNeeded()
}