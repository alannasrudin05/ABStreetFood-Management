// File: StockRepository.kt
package com.praktikum.abstreetfood_management.data.repository

import com.praktikum.abstreetfood_management.data.local.dao.InventorySupplyDao
import com.praktikum.abstreetfood_management.data.local.dao.StockItemDao
import com.praktikum.abstreetfood_management.data.mapper.toDomain
import com.praktikum.abstreetfood_management.data.mapper.toEntity
import com.praktikum.abstreetfood_management.domain.model.InventorySupply
import com.praktikum.abstreetfood_management.domain.model.StockItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepository @Inject constructor(
    private val stockItemDao: StockItemDao,
    private val inventorySupplyDao: InventorySupplyDao, // Asumsi ada DAO ini
) : IStockRepository {

    // --- CRUD Master Bahan Baku (StockItem) ---

    override suspend fun insertStockItem(item: StockItem) {
//        stockItemDao.insertStockItem(item.toEntity()) // Asumsi fungsi di Dao
//        Timber.d("Stock item inserted locally: ${item.name}")
    }

    override fun getAllStockItems(): Flow<List<StockItem>> {
        return stockItemDao.getAllStockItems().map { list ->
            list.map { it.toDomain() } // Asumsi mapper ada
        }
    }

//    override suspend fun getStockItemById(stockItemId: String): StockItem? {
////        return stockItemDao.getStockItemById(stockItemId)?.toDomain() // Asumsi fungsi di Dao
//    }

    // --- CRUD Stok Masuk (Supply) ---

    override suspend fun insertSupplyRecord(record: InventorySupply) {
        // 1. Catat record suplai
//        inventorySupplyDao.insertSupplyRecord(record.toEntity()) // Asumsi fungsi di Dao
//
//        // 2. Update currentStock di StockItemEntity (Optimasi: ini bisa di trigger di UseCase)
//        val currentItem = stockItemDao.getStockItemById(record.stockItemId)
//        if (currentItem != null) {
//            val newStock = currentItem.currentStock + record.quantity
//            val updatedItem = currentItem.copy(currentStock = newStock)
//            stockItemDao.insertStockItem(updatedItem)
//        }

        Timber.d("Supply recorded and stock updated for: ${record.stockItemId}")
    }

//    override fun getSupplyHistory(outletId: String): Flow<List<InventorySupply>> {
////        return inventorySupplyDao.getSupplyHistoryByOutlet(outletId).map { list ->
////            list.map { it.toDomain() }
////        }
//    }

    // --- Sinkronisasi ---

    override suspend fun syncStockAndSupply() {
        Timber.i("Starting stock and supply background sync...")
        // Logika Sync:
        // 1. PULL Master StockItem dari remote
        // 2. PUSH InventorySupply yang belum disinkronkan ke remote
        Timber.i("Stock and supply sync finished.")
    }
}