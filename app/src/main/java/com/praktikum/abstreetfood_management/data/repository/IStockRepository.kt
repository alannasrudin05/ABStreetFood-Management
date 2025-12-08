package com.praktikum.abstreetfood_management.data.repository

import com.praktikum.abstreetfood_management.domain.model.StockItem
import com.praktikum.abstreetfood_management.domain.model.InventorySupply
import kotlinx.coroutines.flow.Flow

interface IStockRepository {
    // CRUD Master Bahan Baku (StockItem)
    suspend fun insertStockItem(item: StockItem)
    fun getAllStockItems(): Flow<List<StockItem>>
//    suspend fun getStockItemById(stockItemId: String): StockItem?

    // CRUD Stok Masuk (Supply)
    suspend fun insertSupplyRecord(record: InventorySupply)
//    fun getSupplyHistory(outletId: String): Flow<List<InventorySupply>>

    // Sinkronisasi
    suspend fun syncStockAndSupply()
}