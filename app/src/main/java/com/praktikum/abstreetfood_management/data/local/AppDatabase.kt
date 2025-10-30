package com.praktikum.abstreetfood_management.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.praktikum.abstreetfood_management.data.local.dao.UserDao
import com.praktikum.abstreetfood_management.data.local.entity.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@Database(entities = [UserEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

//        fun getInstance(context: Context): AppDatabase =
//            INSTANCE ?: synchronized(this) {
//                INSTANCE ?: Room.databaseBuilder(
//                    context.applicationContext,
//                    Ap pDatabase::class.java, "app_database"
//                ).fallbackToDestructiveMigration().build()
//            }

fun getInstance(context: Context): AppDatabase =
    INSTANCE ?: synchronized(this) {
        INSTANCE ?: Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java, "app_database"
        )
            .fallbackToDestructiveMigration()
            .addCallback(DatabaseCallback(context)) // Tambahkan callback untuk data uji
            .build().also { INSTANCE = it }
    }
    }
    /**
     * Callback untuk menjalankan data uji (seeder) saat database dibuat.
     */
    private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                // Jalankan seeding di Coroutine Scope
                CoroutineScope(Dispatchers.IO).launch {
                    seedUsers(database.userDao())
                }
            }
        }

        /**
         * Fungsi untuk menyuntikkan data user uji.
         */
        private suspend fun seedUsers(userDao: UserDao) {
            // Data Uji 1: Admin
            val adminUser = UserEntity(
                id = UUID.randomUUID().toString(),
                name = "Admin Uji",
                email = "admin@test.com",
                password = "password123", // Password TIDAK di-hash
                role = "admin"
            )
            // Data Uji 2: Cashier
            val cashierUser = UserEntity(
                id = UUID.randomUUID().toString(),
                name = "Kasir Toko",
                email = "kasir@test.com",
                password = "kasir123", // Password TIDAK di-hash
                role = "cashier"
            )

//            userDao.insertUser(adminUser)
//            userDao.insertUser(cashierUser)

            // Perhatian: Karena kita menggunakan UUID.randomUUID(), data baru akan dibuat
            // setiap kali database dibuat ulang (misalnya setelah uninstall/clear data).
        }
    }
}
