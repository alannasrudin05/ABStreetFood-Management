package com.praktikum.abstreetfood_management.domain.usecase

import com.praktikum.abstreetfood_management.data.mapper.toDomain
import com.praktikum.abstreetfood_management.data.repository.IUserRepository
import com.praktikum.abstreetfood_management.domain.Resource
import com.praktikum.abstreetfood_management.domain.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

/**
 * AuthUseCase: Mengandung logika bisnis untuk Login/Register.
 * TUGAS: Memvalidasi data, berkomunikasi dengan Repository, dan mengelola state (Resource).
 */
class AuthUseCase @Inject constructor(
    private val userRepository: IUserRepository
) {
    /**
     * Fungsi utama untuk Login. Menerapkan Local-First dengan prioritas Remote.
     * @return Flow Resource<User> untuk menangani state loading/success/error di UI.
     */
    fun login(email: String, password: String): Flow<Resource<User>> = flow {
        emit(Resource.Loading())

        try {
            // 1. COBA LOGIN DARI REMOTE (TURSO)
            val remoteUserEntity = userRepository.loginRemote(email, password)

            if (remoteUserEntity != null) {
                // 2. REMOTE SUCCESS: Simpan ke local (untuk Local-First)
                userRepository.saveUserLocal(remoteUserEntity)

                // 3. Kembalikan User Domain Model
                emit(Resource.Success(remoteUserEntity.toDomain()))
                Timber.d("Login successful via Turso, user saved locally.")
            } else {
                // 4. REMOTE FAILED: Coba fallback ke Local/Room
                val localUserEntity = userRepository.loginLocal(email, password)

                if (localUserEntity != null) {
                    emit(Resource.Success(localUserEntity.toDomain()))
                    Timber.d("Login successful via Room (Offline Mode).")
                } else {
                    emit(Resource.Error("Email atau kata sandi tidak valid."))
                }
            }

        } catch (e: Exception) {
            Timber.e(e, "Error during login process")
            // 5. PENANGANAN ERROR JARINGAN: Coba fallback ke Local/Room
            val localUserEntity = userRepository.loginLocal(email, password)
            if (localUserEntity != null) {
                emit(Resource.Success(localUserEntity.toDomain()))
                emit(Resource.Error("Gagal terhubung ke server. Menggunakan data lokal."))
            } else {
                emit(Resource.Error("Gagal terhubung ke server. Coba lagi."))
            }
        }
    }
}
