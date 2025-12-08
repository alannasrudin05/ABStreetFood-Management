package com.praktikum.abstreetfood_management.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.praktikum.abstreetfood_management.data.local.entity.OutletEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OutletDao {

    /** Menyimpan atau mengganti data outlet. Digunakan untuk Create dan Update. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutlet(outlet: OutletEntity)

    /** Memperbarui data outlet yang sudah ada. */
    @Update
    suspend fun updateOutlet(outlet: OutletEntity)

    /** Menghapus data outlet berdasarkan ID. */
    @Query("DELETE FROM outlet WHERE id = :id")
    suspend fun deleteOutletById(id: String)

    // --- READ FUNCTIONS ---

    /** Mengambil data outlet berdasarkan ID (Diperlukan untuk inisialisasi Shift/Transaksi). */
    @Query("SELECT * FROM outlet WHERE id = :id LIMIT 1")
    suspend fun getOutletById(id: String): OutletEntity?

    /** Mengambil semua outlet yang aktif (Diperlukan untuk Dashboard Owner). */
//    @Query("SELECT * FROM outlet WHERE isActive = 1 ORDER BY name ASC")
//    fun getAllActiveOutlets(): Flow<List<OutletEntity>>

    /** Mengambil list semua outlet (termasuk yang tidak aktif) */
    @Query("SELECT * FROM outlet ORDER BY name ASC")
    fun getAllOutlets(): Flow<List<OutletEntity>>
}