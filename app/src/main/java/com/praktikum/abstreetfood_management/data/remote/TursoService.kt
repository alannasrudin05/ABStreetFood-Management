//package com.praktikum.abstreetfood_management.data.remote
//
//import com.praktikum.abstreetfood_management.data.local.entity.UserEntity
//import com.praktikum.abstreetfood_management.data.remote.response.UserDto
//
//class TursoService(private val client: TursoClient) {
//
//    suspend fun fetchAllUsers(): List<UserEntity> {
//        return client.getUsers()
//    }
//    /**
//     * Bertanggung jawab untuk memanggil TursoClient (HTTP) untuk otentikasi.
//     * Mengembalikan UserEntity (atau null jika login gagal).
//     */
//    suspend fun login(email: String, password: String): UserDto? {
//        // Panggil fungsi login dari TursoClient
//
//        return client.login(email, password)
//    }
//    /**
//     * Bertanggung jawab untuk memanggil TursoClient (HTTP) untuk pendaftaran.
//     * Mengembalikan UserEntity yang baru didaftarkan (atau null jika gagal).
//     */
//    suspend fun register(user: UserEntity): UserEntity? {
//        // Panggil fungsi register dari TursoClient
//        return client.register(user)
//    }
//
//}
