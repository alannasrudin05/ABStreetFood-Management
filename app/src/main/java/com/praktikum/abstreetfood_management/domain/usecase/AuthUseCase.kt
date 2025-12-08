package com.praktikum.abstreetfood_management.domain.usecase

import android.util.Log
import com.praktikum.abstreetfood_management.data.mapper.toDomain
import com.praktikum.abstreetfood_management.data.mapper.toEntity
import com.praktikum.abstreetfood_management.data.repository.IUserRepository
import com.praktikum.abstreetfood_management.utility.Resource
import com.praktikum.abstreetfood_management.domain.model.User
import com.praktikum.abstreetfood_management.utility.PasswordHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

/**
 * AuthUseCase: Mengandung logika bisnis untuk Login/Register.
 * TUGAS: Memvalidasi data, berkomunikasi dengan Repository, dan mengelola state (Resource).
 */
class AuthUseCase @Inject constructor(
    private val userRepository: IUserRepository,
    private val passwordHelper: PasswordHelper
) {


    /**
     * Fungsi utama untuk Login. Menerapkan Local-First dengan prioritas Remote.
     * @return Flow Resource<User> untuk menangani state loading/success/error di UI.
     */
    fun login(email: String, password: String): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        Timber.d("AUTH_USECASE: Memulai alur login untuk email: $email") // <<< LOG BARU

        try {
            // 1. COBA LOGIN DARI REMOTE (TURSO)
            val passwordHash = passwordHelper.hashPassword(password)
            Timber.d("AUTH_USECASE: Langkah 1: Mencoba login Remote (Turso).") // <<< LOG BARU
            val remoteUserEntity = userRepository.loginRemote(email, password)

            if (remoteUserEntity != null) {
                // 2. REMOTE SUCCESS: Simpan ke local (untuk Local-First)
                Timber.i("AUTH_USECASE: Langkah 2: Remote Sukses. Menyimpan data user ke Room.") // <<< LOG BARU
                userRepository.saveUserLocal(remoteUserEntity)

                // 3. Kembalikan User Domain Model
                emit(Resource.Success(remoteUserEntity.toDomain()))

                Timber.d("AUTH_USECASE: Login berhasil via Turso, user disimpan lokal.")
                Timber.d("Login successful via Turso, user saved locally.")
            } else {
//                // 4. REMOTE FAILED: Coba fallback ke Local/Room
//                Timber.w("AUTH_USECASE: Langkah 3: Remote Gagal (Kredensial Salah/Null). Mencoba login Local (Room).") // <<< LOG BARU
//                val localUserEntity = userRepository.loginLocal(email, password)
//
//                if (localUserEntity != null) {
//                    emit(Resource.Success(localUserEntity.toDomain()))
//                    Timber.d("AUTH_USECASE: Login berhasil via Room (Mode Offline).")
//                } else {
//                    emit(Resource.Error("Email atau kata sandi tidak valid."))
//                    Timber.e("AUTH_USECASE: Login Gagal total. Kredensial tidak valid di Remote/Local.") // <<< LOG BARU
//                }
                // 4. REMOTE FAILED: Coba fallback ke Local/Room
                Timber.w("AUTH_USECASE: Langkah 3: Remote Gagal. Mencoba login Local (Room).")

                // KOREKSI UTAMA DIMULAI DI SINI:
                val localUserEntity = userRepository.getUserLocal(email) // Ambil user berdasarkan email saja

                Timber.tag("CEKPASSWORD").d(localUserEntity?.password, password)
                if (localUserEntity != null) {
                    // VERIFIKASI PASSWORD MENGGUNAKAN HASH
                    if (passwordHelper.verifyPassword(password, localUserEntity.password)) {
                        // VERIFIKASI HASH SUKSES!
                        emit(Resource.Success(localUserEntity.toDomain()))
                        Timber.d("AUTH_USECASE: Login berhasil via Room (Mode Offline) dengan verifikasi hash.")
                    } else {
                        // VERIFIKASI HASH GAGAL
                        emit(Resource.Error("Email atau kata sandi tidak valid."))
                        Timber.e("AUTH_USECASE: Login Gagal total. Password hash tidak cocok.")
                    }
                } else {
                    // USER DENGAN EMAIL TERSEBUT TIDAK DITEMUKAN SECARA LOKAL
                    emit(Resource.Error("Email atau kata sandi tidak valid."))
                    Timber.e("AUTH_USECASE: Login Gagal total. Kredensial tidak valid di Remote/Local.")
                }
            }

        } catch (e: Exception) {
            Timber.e(e, "Error during login process")
//            // 5. PENANGANAN ERROR JARINGAN: Coba fallback ke Local/Room
//            val localUserEntity = userRepository.loginLocal(email, password)
//            if (localUserEntity != null) {
//                emit(Resource.Success(localUserEntity.toDomain()))
//                emit(Resource.Error("Gagal terhubung ke server. Menggunakan data lokal."))
//            } else {
//                emit(Resource.Error("Gagal terhubung ke server. Coba lagi."))
//                Timber.e("AUTH_USECASE: Fallback Local Gagal. Aplikasi tidak bisa login.")
//            }
            // KOREKSI UNTUK FALLBACK:
            val localUserEntity = userRepository.getUserLocal(email) // Ambil user berdasarkan email

            if (localUserEntity != null && passwordHelper.verifyPassword(password, localUserEntity.password)) {
                // Fallback sukses dan verifikasi hash sukses
                emit(Resource.Success(localUserEntity.toDomain()))
                emit(Resource.Error("Gagal terhubung ke server. Menggunakan data lokal."))
            } else {
                emit(Resource.Error("Gagal terhubung ke server. Coba lagi."))
                Timber.e("AUTH_USECASE: Fallback Local Gagal atau password salah.")
            }
        }
    }

    fun register(name: String, email: String, password: String): Flow<Resource<User>> = flow {
        emit(Resource.Loading())

        // 1. Buat UserEntity baru (dengan ID unik lokal)
        val newUserEntity = User(
            id = UUID.randomUUID().toString(),
            name = name,
            email = email,
            role = "cashier", // Default role
            isActive = true
        ).toEntity(password)

        try {
            // 2. COBA REGISTER DARI REMOTE (Turso) - Saat ini akan mengembalikan NULL
//            val remoteUserEntity = userRepository.registerRemote(newUserEntity)
//
//            if (remoteUserEntity != null) {
//                // Turso SUCCESS (akan bekerja saat diaktifkan)
//                userRepository.saveUserLocal(remoteUserEntity)
//                emit(Resource.Success(remoteUserEntity.toDomain()))
//                Timber.d("Register successful via Turso, user saved locally.")
//            } else {
//                // 3. REMOTE FAILED: Register ke Local/Room
//                val localUserEntity = userRepository.registerLocal(newUserEntity)
//
//                if (localUserEntity != null) {
//                    emit(Resource.Success(localUserEntity.toDomain()))
//                    Timber.d("Register successful via Room (Offline Mode).")
//                } else {
//                    emit(Resource.Error("Email sudah terdaftar secara lokal. Coba login."))
//                }
//            }
            Log.d("REGISTER_FLOW", "4. Attempting Remote Registration...")
            val remoteUserDto = userRepository.registerRemote(name, email, password)

            if (remoteUserDto != null) {
                // Log 5a: Remote SUCCESS
                Log.i("REGISTER_FLOW", "5a. Remote SUCCESS. Saving local copy.")
                val userEntity = remoteUserDto.toEntity(password)
                userRepository.saveUserLocal(userEntity)
                emit(Resource.Success(userEntity.toDomain()))
//            } else {
//                // Log 5b: Remote FAILED, Fallback ke Lokal
//                Log.w("REGISTER_FLOW", "5b. Remote FAILED. Attempting Local Registration Fallback.")
//
////                val localUserEntity = userRepository.registerLocal(newUserEntity)
//
//                if (localUserEntity != null) {
//                    // Log 6a: Local SUCCESS
//                    Log.i("REGISTER_FLOW", "6a. Local SUCCESS. User created in Room.")
//                    emit(Resource.Success(localUserEntity.toDomain()))
//                } else {
//                    // Log 6b: Local FAILED (Email sudah ada)
//                    Log.e("REGISTER_FLOW", "6b. Local FAILED (Email already exists).")
//                    emit(Resource.Error("Email sudah terdaftar."))
//                }
            }

        } catch (e: Exception) {
            Timber.e(e, "Error during registration process: ${e.message}")
            // 4. PENANGANAN ERROR JARINGAN: Coba Register Lokal sebagai fallback
//            val localUserEntity = userRepository.registerLocal(newUserEntity)

//            if (localUserEntity != null) {
//                emit(Resource.Success(localUserEntity.toDomain()))
//                emit(Resource.Error("Gagal terhubung ke server Turso. Akun berhasil dibuat secara lokal."))
//            } else {
//                emit(Resource.Error("Gagal terhubung ke server Turso dan email sudah terdaftar secara lokal."))
//            }
        }
    }
}
