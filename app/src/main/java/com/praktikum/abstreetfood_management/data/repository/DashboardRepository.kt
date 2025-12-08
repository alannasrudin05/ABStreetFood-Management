package com.praktikum.abstreetfood_management.data.repository

import com.praktikum.abstreetfood_management.data.local.dao.ProductDao
import com.praktikum.abstreetfood_management.data.local.dao.StockItemDao
import com.praktikum.abstreetfood_management.data.local.dao.TransactionDao
import com.praktikum.abstreetfood_management.data.local.dao.TransactionItemDao
import com.praktikum.abstreetfood_management.data.local.entity.ProductEntity
import com.praktikum.abstreetfood_management.data.local.entity.StockItemEntity
import com.praktikum.abstreetfood_management.domain.model.TopSellingProduct
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val productDao: ProductDao,
    private val transactionItemDao: TransactionItemDao,
    private val stockItemDao: StockItemDao
) {
    /** Mendapatkan total pendapatan harian untuk user yang sedang login */
    fun getDailyRevenue(userId: String, startOfDay: Long): Flow<Double?> {
        return transactionDao.getDailyRevenue(userId, startOfDay)
    }

    // [BARU DITAMBAH] Mendapatkan total transaksi harian
    fun getDailyTransactionCount(userId: String, startOfDay: Long): Flow<Int?> {
        // Asumsi query ini sudah ada di TransactionDao (misal: COUNT(id) WHERE userId = :userId AND timestamp >= :startOfDay)
        return transactionDao.getDailyTransactionCount(userId, startOfDay)
    }

    /** Mendapatkan produk terlaris */
    fun getTopSellingProducts(): Flow<List<TopSellingProduct>> {
        return transactionItemDao.getTopSellingProducts()
    }

    // [SESUAIKAN DENGAN RENCANA] Mendapatkan SEMUA Produk untuk Menu/Daftar Lengkap
    fun getAllProducts(): Flow<List<ProductEntity>> {
        return productDao.getAllProducts()
    }

    // [SESUAIKAN DENGAN RENCANA] Mendapatkan SEMUA Stok Bahan (Akan difilter di ViewModel)
    fun getAllStockItems(): Flow<List<StockItemEntity>> {
        return stockItemDao.getAllStockItems()
    }
}