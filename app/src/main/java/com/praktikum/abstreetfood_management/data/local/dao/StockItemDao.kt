package com.praktikum.abstreetfood_management.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.praktikum.abstreetfood_management.data.local.entity.StockItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StockItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockItems(items: List<StockItemEntity>)

    // Query untuk mendapatkan SEMUA Stok Bahan Baku (sesuai kebutuhan Repository)
    @Query("SELECT * FROM stock_items ORDER BY itemName ASC")
    fun getAllStockItems(): Flow<List<StockItemEntity>>

    // Anda bisa menambahkan query filter Warning di sini,
    // TAPI SAYA SARANKAN FILTERING TETAP DI VIEWMODEL/REPOSITORY
    // agar data penuh tetap diamati.
}