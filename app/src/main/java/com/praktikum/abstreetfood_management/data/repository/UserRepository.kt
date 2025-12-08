package com.praktikum.abstreetfood_management.data.repository

import android.util.Log
import com.praktikum.abstreetfood_management.data.local.AppDatabase
import com.praktikum.abstreetfood_management.data.local.entity.UserEntity
import com.praktikum.abstreetfood_management.data.mapper.toEntity
import com.praktikum.abstreetfood_management.data.network.ApiService
import com.praktikum.abstreetfood_management.data.remote.response.UserDto
import com.praktikum.abstreetfood_management.domain.LoginRequest
import com.praktikum.abstreetfood_management.domain.RegisterRequest
import com.praktikum.abstreetfood_management.utility.PasswordHelper
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val localDb: AppDatabase,
    private val remoteService: ApiService,
    private val authPrefs: IAuthPreferences,
    private val passwordHelper: PasswordHelper
) : IUserRepository {

    private val userDao = localDb.userDao()
    private val TAG = "USER_REPO"

    /**
     * 1. Fungsi Login Remote (Turso) - REMOTE FIRST
     * Sekarang mengkonversi UserDto dari service ke UserEntity.
     */
    override suspend fun loginRemote(email: String, password: String): UserEntity? {
        val remoteUserEntity = try {

            Timber.d("USER_REPO: Mencoba login ke Turso untuk email: $email")

//            // Panggil TursoService yang kini mengembalikan UserDto?
//            val userDto = remoteService.login(email, password) // <<< MENDAPATKAN UserDto
//
//            if (userDto != null) {
//                Timber.i("USER_REPO: Login Remote SUCCESS. User ID: ${userDto.id}, Role: ${userDto.role}")
//
//                // KONVERSI: UserDto (Remote) -> UserEntity (Local)
//                // Kita harus mengisi kolom-kolom Entity yang tidak ada di DTO (password, createdAt, etc.)
//                // Karena kita menggunakan password teks biasa di DB Lokal untuk fallback:
//                userDto.toEntity(password) // <<< Gunakan Mapper.toEntity(password)
//
//            } else {
//                Timber.w("USER_REPO: Login Remote FAILED. Turso tidak mengembalikan data.")
//                null
//            }
            // 1. Panggil TursoService menggunakan objek LoginRequest
            val loginRequest = LoginRequest(email, password)
            val response = remoteService.loginUser(loginRequest) // <<< PANGGILAN RETROFIT

            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!

                authPrefs.saveAuthToken(loginResponse.token)
//                authPrefs.saveUserSession(loginResponse.user.id, loginResponse.user.role)

                val hashedPassword = passwordHelper.hashPassword(password)
                // 🚨 PENYESUAIAN KRUSIAL: SIMPAN TOKEN JWT DI SINI!
                // Token harus disimpan di Local Storage (misalnya SharedPreferences/DataStore)
                // Fungsi ini (saveToken) TIDAK ADA DI REPO saat ini, Anda harus membuatnya.
                // sharedPrefsManager.saveAuthToken(loginResponse.token)

                val userId = loginResponse.user.id
                val userRole = loginResponse.user.role
                val userName = loginResponse.user.name
                authPrefs.saveUserSession(userId, userRole, userName)

                Timber.i("USER_REPO: Token dan Session ID disimpan: $userId")
                Timber.i("USER_REPO: Login Remote SUCCESS. Token diterima.")

                // 2. KONVERSI: UserDto dari LoginResponse -> UserEntity (Local)
                // Asumsi: loginResponse.user adalah UserDto
                loginResponse.user.toEntity(hashedPassword) // <<< Gunakan Mapper.toEntity(password)

            } else {
                // Handle HTTP error code (401, 403, etc.)
                Timber.w("USER_REPO: Login Remote FAILED. HTTP Code: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "USER_REPO: Login Remote EXCEPTION (Network/Service Error).")
            null
        }
        return remoteUserEntity
    }

    /**
     * 2. Fungsi Login Local (Room) - FALLBACK
     * Digunakan jika jaringan mati atau login remote gagal.
     */
    override suspend fun loginLocal(email: String, password: String): UserEntity? {
//        return userDao.getUserByCredentials(email, password)
        val localUser = userDao.getUserByCredentials(email, password)
        if (localUser != null) {
            Timber.i("USER_REPO: Login Local SUCCESS. Menggunakan data Room (Offline).") // <<< LOG BARU
        } else {
            Timber.w("USER_REPO: Login Local FAILED. User tidak ditemukan di Room.") // <<< LOG BARU
        }
        return localUser
    }

    /**
     * 3. Register Remote (Turso)
     * Mengaktifkan panggilan ke remoteService.register()
     */
    override suspend fun registerRemote(name: String, email: String, password: String): UserDto? {
        val registerRequest = RegisterRequest(name, email, password)

        try {
            val response = remoteService.registerUser(registerRequest)

            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "USER_REPO: Register Remote SUCCESS.")
                return response.body() // Mengembalikan UserDto yang baru dibuat
            } else {
                Log.w(TAG,"USER_REPO: Register Remote FAILED. HTTP Code: ${response.code()}, Error: ${response.errorBody()?.string()}")
                return null
            }
        } catch (e: Exception) {
            Log.e(TAG, "USER_REPO: Register Remote EXCEPTION (Network/Service Error).", e)
            return null
        }
    }

    /**
     * 4. Register Local (Room) - FALLBACK
     */
//    override suspend fun registerLocal(user: UserEntity): UserEntity? {
//        // Cek apakah user sudah terdaftar di lokal
//        val existingUser = userDao.getUserByEmail(user.email)
//        return if (existingUser == null) {
//            userDao.insertUser(user)
//            user // Berhasil
//        } else {
//            null // User sudah ada
//        }
//    }

    /**
     * 5. Fungsi Simpan ke Local (untuk update data user terbaru dari remote)
     */
    override suspend fun saveUserLocal(user: UserEntity) {
        userDao.insertUser(user)
    }

    override suspend fun getUserLocal(email: String): UserEntity? {
        // Memanggil fungsi DAO baru: getUserByEmail
        return userDao.getUserByEmail(email)
    }
    /**
     * 6. Sinkronisasi data umum (getUsers)
     */
//    override suspend fun getUsers(forceRefresh: Boolean): List<UserEntity> {
//        return if (forceRefresh) {
//            // Logika Force Refresh: Ambil remote, hapus local, simpan remote
//            val remoteUsers = remoteService.fetchAllUsers()
//            userDao.clearAll()
//            userDao.insertUsers(remoteUsers)
//            remoteUsers
//        } else {
//            val localUsers = userDao.getAllUsers()
//            if (localUsers.isEmpty()) {
//                // Fetch dari remote jika local kosong (Cache miss)
//                val remoteUsers = remoteService.fetchAllUsers()
//                userDao.insertUsers(remoteUsers)
//                remoteUsers
//            } else {
//                localUsers
//            }
//        }
//    }

    /**
     * 7. Sinkronisasi di Latar Belakang (Dipanggil oleh WorkManager)
     */
//    override suspend fun syncIfNeeded() {
//        try {
//            // Ambil semua data remote terbaru
//            val remoteUsers = remoteService.fetchAllUsers()
//            // Simpan/perbarui data remote ke local (OnConflictStrategy.REPLACE)
//            userDao.insertUsers(remoteUsers)
//            Timber.d("Background sync completed successfully.")
//        } catch (e: Exception) {
//            // Kegagalan sinkronisasi tidak memblokir aplikasi
//            Timber.e(e, "Background sync failed. Running on local data.")
//        }
//    }

    /** Memperbarui detail pengguna di lokal dan remote (jika ada) */
//    override suspend fun updateUser(user: UserEntity): Result<Unit> {
//        return try {
//            // 1. Update ke Remote (Turso)
//            // val remoteResult = remoteService.updateUser(user) // Asumsi ada fungsi ini
//
//            // 2. Update ke Lokal (Room)
//            userDao.insertUser(user) // Menggunakan INSERT/REPLACE karena efisien
//
//            Timber.d("USER_REPO: User updated locally: ${user.email}")
//            Result.success(Unit)
//        } catch (e: Exception) {
//            Timber.e(e, "USER_REPO: Failed to update user: ${user.email}")
//            Result.failure(e)
//        }
//    }

    /** Menghapus pengguna dari lokal dan remote (jika ada) */
//    override suspend fun deleteUser(userId: String): Result<Unit> {
//        return try {
//            // 1. Hapus dari Remote (Turso)
//            // remoteService.deleteUser(userId) // Asumsi ada fungsi ini
//
//            // 2. Hapus dari Lokal (Room)
//            userDao.deleteUserById(userId) // Asumsi userDao punya fungsi deleteUserById
//
//            Timber.d("USER_REPO: User deleted: $userId")
//            Result.success(Unit)
//        } catch (e: Exception) {
//            Timber.e(e, "USER_REPO: Failed to delete user: $userId")
//            Result.failure(e)
//        }
//    }
}

//package com.praktikum.abstreetfood_management.data.repository
//
//import com.praktikum.abstreetfood_management.data.local.AppDatabase
//import com.praktikum.abstreetfood_management.data.local.entity.UserEntity
//import com.praktikum.abstreetfood_management.data.remote.TursoService
//import timber.log.Timber
//
//class UserRepository(
//    private val localDb: AppDatabase,
//    private val remoteService: TursoService
//) {
//
//    private val userDao = localDb.userDao()
//
//    suspend fun getUsers(forceRefresh: Boolean = false): List<UserEntity> {
//        return if (forceRefresh) {
//            val remoteUsers = remoteService.fetchAllUsers()
//            userDao.clearAll()
//            userDao.insertUsers(remoteUsers)
//            remoteUsers
//        } else {
//            val localUsers = userDao.getAllUsers()
//            if (localUsers.isEmpty()) {
//                val remoteUsers = remoteService.fetchAllUsers()
//                userDao.insertUsers(remoteUsers)
//                remoteUsers
//            } else {
//                localUsers
//            }
//        }
//    }
//
//    suspend fun syncIfNeeded() {
//        try {
//            val remoteUsers = remoteService.fetchAllUsers()
//            userDao.insertUsers(remoteUsers)
//        } catch (e: Exception) {
//            Timber.e(e, "Sync failed")
//        }
//    }
//}

//
//package com.praktikum.abstreetfood_management.data.repository
//
//import com.praktikum.abstreetfood_management.data.local.AppDatabase
//import com.praktikum.abstreetfood_management.data.local.entity.UserEntity
//import com.praktikum.abstreetfood_management.data.remote.TursoService
//import timber.log.Timber
//import javax.inject.Inject
//
//
//
//class UserRepository @Inject constructor(
//    private val localDb: AppDatabase,
//    private val remoteService: TursoService
//) : IUserRepository {
//
//    private val userDao = localDb.userDao()
//
//    /**
//     * 1. Fungsi Login Remote (Turso)
//     */
//    override suspend fun loginRemote(email: String, password: String): UserEntity? {
//        // TursoClient harusnya mengembalikan UserEntity atau DTO yang bisa diconvert
//        return remoteService.login(email, password)
//    }
//
//    /**
//     * 2. Fungsi Login Local (Room)
//     */
//    override suspend fun loginLocal(email: String, password: String): UserEntity? {
//        return userDao.getUserByCredentials(email, password)
//    }
//
//    /**
//     * 3. Register Remote (Turso) - SEMENTARA DINOAKTIFKAN
//     */
//    override suspend fun registerRemote(user: UserEntity): UserEntity? {
//        Timber.d("Turso register is temporarily disabled. Skipping remote call.")
//        // return remoteService.register(user) // <--- Jika ingin mengaktifkan Turso
//        return null // <-- PAKSA GAGAL, agar Use Case mengarah ke Local
//    }
//
//    /**
//     * 4. Register Local (Room)
//     */
//    override suspend fun registerLocal(user: UserEntity): UserEntity? {
//        // Cek apakah user sudah terdaftar di lokal
//        val existingUser = userDao.getUserByEmail(user.email)
//        return if (existingUser == null) {
//            userDao.insertUser(user)
//            user // Berhasil
//        } else {
//            null // User sudah ada
//        }
//    }
//
//    /**
//     * 3. Fungsi Simpan ke Local
//     */
//    override suspend fun saveUserLocal(user: UserEntity) {
//        userDao.insertUser(user)
//    }
//
//    /**
//     * 4. Sinkronisasi data umum (getUsers)
//     */
//    override suspend fun getUsers(forceRefresh: Boolean): List<UserEntity> {
//        return if (forceRefresh) {
//            // Logika Force Refresh
//            val remoteUsers = remoteService.fetchAllUsers()
//            userDao.clearAll()
//            userDao.insertUsers(remoteUsers)
//            remoteUsers
//        } else {
//            val localUsers = userDao.getAllUsers()
//            if (localUsers.isEmpty()) {
//                // Fetch dari remote jika local kosong (Cache miss)
//                val remoteUsers = remoteService.fetchAllUsers()
//                userDao.insertUsers(remoteUsers)
//                remoteUsers
//            } else {
//                localUsers
//            }
//        }
//    }
//
//    /**
//     * 5. Sinkronisasi di Latar Belakang
//     */
//    override suspend fun syncIfNeeded() {
//        try {
//            // Ambil semua data remote terbaru
//            val remoteUsers = remoteService.fetchAllUsers()
//            // Simpan/perbarui data remote ke local (OnConflictStrategy.REPLACE)
//            userDao.insertUsers(remoteUsers)
//            Timber.d("Background sync completed successfully.")
//        } catch (e: Exception) {
//            // Kegagalan sinkronisasi tidak memblokir aplikasi
//            Timber.e(e, "Background sync failed. Running on local data.")
//        }
//    }
//}

