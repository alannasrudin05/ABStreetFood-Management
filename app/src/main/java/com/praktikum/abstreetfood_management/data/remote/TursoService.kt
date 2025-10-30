package com.praktikum.abstreetfood_management.data.remote

import com.praktikum.abstreetfood_management.data.local.entity.UserEntity

class TursoService(private val client: TursoClient) {

    suspend fun fetchAllUsers(): List<UserEntity> {
        return client.getUsers()
    }
    /**
     * Bertanggung jawab untuk memanggil TursoClient (HTTP) untuk otentikasi.
     * Mengembalikan UserEntity (atau null jika login gagal).
     */
    suspend fun login(email: String, password: String): UserEntity? {
        // Panggil fungsi login dari TursoClient
        return client.login(email, password)
    }

}
