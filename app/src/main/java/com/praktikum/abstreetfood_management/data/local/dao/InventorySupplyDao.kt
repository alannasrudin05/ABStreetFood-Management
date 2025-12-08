package com.praktikum.abstreetfood_management.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.praktikum.abstreetfood_management.data.local.entity.InventorySupplyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InventorySupplyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupplyRecord(record: InventorySupplyEntity)

    // Kunci untuk Algoritma RIRA (Mendapatkan Stok Awal Outlet sejak shift/waktu tertentu)
    // Query ini akan digunakan untuk menghitung total suplai bahan tertentu di outlet tertentu
    @Query("""
        SELECT SUM(quantity) 
        FROM inventory_supply 
        WHERE outletId = :outletId AND stockItemId = :stockItemId 
        AND supplyDate >= :startTime
    """)
    fun getSupplyQuantitySince(outletId: String, stockItemId: String, startTime: Long): Flow<Double?>

    // Fungsi untuk mendapatkan semua record supply (untuk history/audit)
    @Query("SELECT * FROM inventory_supply WHERE outletId = :outletId ORDER BY supplyDate DESC")
    fun getSupplyHistory(outletId: String): Flow<List<InventorySupplyEntity>>
}