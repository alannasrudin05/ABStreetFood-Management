package com.praktikum.abstreetfood_management.data.repository

import com.praktikum.abstreetfood_management.data.local.entity.ShiftEntity
import com.praktikum.abstreetfood_management.data.local.entity.TransactionEntity
import com.praktikum.abstreetfood_management.data.local.entity.TransactionItemEntity
import com.praktikum.abstreetfood_management.domain.model.NewTransaction
import com.praktikum.abstreetfood_management.domain.model.NewTransactionItem
import com.praktikum.abstreetfood_management.domain.model.TopSellingProduct
import com.praktikum.abstreetfood_management.domain.model.Transaction
import com.praktikum.abstreetfood_management.domain.model.TransactionDetail
import kotlinx.coroutines.flow.Flow

interface ITransactionRepository {
    // Transaction (CRUD)
//    suspend fun recordNewTransaction(newTransaction: NewTransaction): Result<Unit>
    suspend fun recordNewTransaction(newTransaction: NewTransaction): Result<String>
    fun getDailyRevenueForPeriod(startTime: Long, endTime: Long): Flow<List<DailySalesData>>
    fun getDailyTransactionCount(userId: String): Flow<Int?>

    // ITransactionRepository.kt (Asumsi)

    fun getTopSellingProductsForPeriod(startTime: Long, endTime: Long): Flow<List<TopSellingProduct>>

    /** Mendapatkan detail Transaksi berdasarkan ID */
    suspend fun getTransactionById(transactionId: String): Transaction? // <-- BARU

    /** Mengupdate Transaksi (Digunakan untuk Void/Edit) */
    suspend fun updateTransaction(transaction: TransactionEntity): Result<Unit> // <-- BARU

    /** Mendapatkan daftar transaksi terakhir untuk History/Laporan */
    fun getTransactionHistory(): Flow<List<Transaction>> // <-- BARU

    /** Mendapatkan detail transaksi lengkap untuk Cetak Nota (Header + Items) */
//    suspend fun getTransactionDetail(transactionId: String): TransactionDetail? // <-- BARU (Memerlukan Model TransactionDetail)
    suspend fun getTransactionDetailById(transactionId: String): TransactionDetail? // <-- BARU (Memerlukan Model TransactionDetail)
    // ---------------------------------------------

    // Shift Management
    suspend fun openShift(shift: ShiftEntity)
    suspend fun closeShift(shift: ShiftEntity)
    suspend fun getCurrentOpenShift(userId: String): ShiftEntity?

    // Sync (The Core of Local-First)
    suspend fun synchronizeTransactions(): Boolean
}

data class DailySalesData(
    val dayTimestamp: Long, // Waktu (misalnya, tengah malam)
    val revenue: Double     // Total GrandTotal hari itu
)

data class DailyRevenueRaw(
    val dayTimestamp: Long,
    val totalRevenue: Double
)

