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


package com.praktikum.abstreetfood_management.data.repository

import com.praktikum.abstreetfood_management.data.local.AppDatabase
import com.praktikum.abstreetfood_management.data.local.entity.UserEntity
import com.praktikum.abstreetfood_management.data.remote.TursoService
import timber.log.Timber
import javax.inject.Inject



class UserRepository @Inject constructor(
    private val localDb: AppDatabase,
    private val remoteService: TursoService
) : IUserRepository {

    private val userDao = localDb.userDao()

    /**
     * 1. Fungsi Login Remote (Turso)
     */
    override suspend fun loginRemote(email: String, password: String): UserEntity? {
        // TursoClient harusnya mengembalikan UserEntity atau DTO yang bisa diconvert
        return remoteService.login(email, password)
    }

    /**
     * 2. Fungsi Login Local (Room)
     */
    override suspend fun loginLocal(email: String, password: String): UserEntity? {
        return userDao.getUserByCredentials(email, password)
    }

    /**
     * 3. Fungsi Simpan ke Local
     */
    override suspend fun saveUserLocal(user: UserEntity) {
        userDao.insertUser(user)
    }

    /**
     * 4. Sinkronisasi data umum (getUsers)
     */
    override suspend fun getUsers(forceRefresh: Boolean): List<UserEntity> {
        return if (forceRefresh) {
            // Logika Force Refresh
            val remoteUsers = remoteService.fetchAllUsers()
            userDao.clearAll()
            userDao.insertUsers(remoteUsers)
            remoteUsers
        } else {
            val localUsers = userDao.getAllUsers()
            if (localUsers.isEmpty()) {
                // Fetch dari remote jika local kosong (Cache miss)
                val remoteUsers = remoteService.fetchAllUsers()
                userDao.insertUsers(remoteUsers)
                remoteUsers
            } else {
                localUsers
            }
        }
    }

    /**
     * 5. Sinkronisasi di Latar Belakang
     */
    override suspend fun syncIfNeeded() {
        try {
            // Ambil semua data remote terbaru
            val remoteUsers = remoteService.fetchAllUsers()
            // Simpan/perbarui data remote ke local (OnConflictStrategy.REPLACE)
            userDao.insertUsers(remoteUsers)
            Timber.d("Background sync completed successfully.")
        } catch (e: Exception) {
            // Kegagalan sinkronisasi tidak memblokir aplikasi
            Timber.e(e, "Background sync failed. Running on local data.")
        }
    }
}

