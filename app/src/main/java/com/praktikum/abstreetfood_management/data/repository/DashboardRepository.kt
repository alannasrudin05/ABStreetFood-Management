package com.praktikum.abstreetfood_management.data.repository

import android.util.Log
import com.praktikum.abstreetfood_management.data.local.dao.ProductDao
import com.praktikum.abstreetfood_management.data.local.dao.StockItemDao
import com.praktikum.abstreetfood_management.data.local.dao.TransactionDao
import com.praktikum.abstreetfood_management.data.local.dao.TransactionItemDao
import com.praktikum.abstreetfood_management.data.local.entity.ProductEntity
import com.praktikum.abstreetfood_management.data.local.entity.StockItemEntity
import com.praktikum.abstreetfood_management.domain.model.DailyMetric
import com.praktikum.abstreetfood_management.domain.model.TopSellingProduct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val productDao: ProductDao,
    private val transactionItemDao: TransactionItemDao,
    private val stockItemDao: StockItemDao
) {

    fun getRevenueMetrics(userId: String, startOfDay: Long, startOfYesterday: Long): Flow<DailyMetric> {
        val currentRevenueFlow = transactionDao.getDailyRevenue(userId, startOfDay)
        val previousRevenueFlow = transactionDao.getPreviousDailyRevenue(userId, startOfYesterday, startOfDay)

        return currentRevenueFlow.combine(previousRevenueFlow) { current, previous ->
            val currentTotal = current ?: 0.0
            val previousTotal = previous ?: 0.0

            val percentageChange = if (previousTotal == 0.0) {
                // Jika kemarin 0, tapi hari ini ada, anggap naik 100% (atau nilai maksimum, misalnya 999.0)
                if (currentTotal > 0) 100.0 else 0.0
            } else {
                ((currentTotal - previousTotal) / previousTotal) * 100.0
            }
            Log.d("REPO_METRIC", "Revenue: Current=${currentTotal}, Prev=${previousTotal}, Change=${"%.2f".format(percentageChange)}%") // ✅ LOG REVENUE

            DailyMetric(
                currentTotal = currentTotal,
                previousTotal = previousTotal,
                percentageChange = percentageChange
            )
        }
    }

    /**
     * ✅ BARU: Mengambil data Transaksi Hari Ini dan Kemarin, lalu menghitung persentase.
     */
    fun getTransactionMetrics(userId: String, startOfDay: Long, startOfYesterday: Long): Flow<DailyMetric> {
        val currentCountFlow = transactionDao.getDailyTransactionCount(userId, startOfDay)
        val previousCountFlow = transactionDao.getPreviousDailyTransactionCount(userId, startOfYesterday, startOfDay)

        return currentCountFlow.combine(previousCountFlow) { current, previous ->
            val currentTotal = (current ?: 0).toDouble()
            val previousTotal = (previous ?: 0).toDouble()

            val percentageChange = if (previousTotal == 0.0) {
                if (currentTotal > 0) 100.0 else 0.0
            } else {
                ((currentTotal - previousTotal) / previousTotal) * 100.0
            }

            Log.d("REPO_METRIC", "Transaction: Current=${currentTotal.toInt()}, Prev=${previousTotal.toInt()}, Change=${"%.2f".format(percentageChange)}%") // ✅ LOG TRANSAKSI

            DailyMetric(
                currentTotal = currentTotal,
                previousTotal = previousTotal,
                percentageChange = percentageChange
            )
        }
    }

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