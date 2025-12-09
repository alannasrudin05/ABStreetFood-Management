package com.praktikum.abstreetfood_management.data.local.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.praktikum.abstreetfood_management.data.local.entity.TransactionEntity
import com.praktikum.abstreetfood_management.data.local.entity.TransactionItemEntity
import com.praktikum.abstreetfood_management.data.repository.DailyRevenueRaw
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE isSynced = 0")
    suspend fun getPendingSyncTransactions(): List<TransactionEntity>

    // Query Statistik Harian untuk HomeFragment Pegawai
    @Query("SELECT SUM(grandTotal) FROM transactions WHERE userId = :userId AND transactionTime >= :startOfDay")
    fun getDailyRevenue(userId: String, startOfDay: Long): Flow<Double?>

    @Query("SELECT COUNT(id) FROM transactions WHERE userId = :userId AND transactionTime >= :startOfDay")
    fun getDailyTransactionCount(userId: String, startOfDay: Long): Flow<Int?>

    @Query("SELECT SUM(grandTotal) FROM transactions WHERE userId = :userId AND transactionTime BETWEEN :startOfYesterday AND :startOfDay")
    fun getPreviousDailyRevenue(userId: String, startOfYesterday: Long, startOfDay: Long): Flow<Double?>

    @Query("SELECT COUNT(id) FROM transactions WHERE userId = :userId AND transactionTime BETWEEN :startOfYesterday AND :startOfDay")
    fun getPreviousDailyTransactionCount(userId: String, startOfYesterday: Long, startOfDay: Long): Flow<Int?>

    /** Mendapatkan semua transaksi untuk History/Laporan (Contoh: 100 transaksi terakhir) */
    @Query("SELECT * FROM transactions ORDER BY transactionTime DESC LIMIT 100")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    /** Query untuk Cetak Nota: Mendapatkan Header Transaksi berdasarkan ID */
    @Query("SELECT * FROM transactions WHERE id = :transactionId")
    suspend fun getTransactionById(transactionId: String): TransactionEntity?

    /** Query Grafik Harian: Mengelompokkan pendapatan berdasarkan hari */
    @Query("""
        SELECT 
            (transactionTime / 86400000) * 86400000 AS dayTimestamp, 
            SUM(grandTotal) AS totalRevenue
        FROM transactions
        WHERE transactionTime BETWEEN :startTime AND :endTime
        GROUP BY dayTimestamp
        ORDER BY dayTimestamp ASC
    """)
    fun getDailyRevenueForPeriod(startTime: Long, endTime: Long): Flow<List<DailyRevenueRaw>>

    /** Query untuk Cetak Nota: Mendapatkan Detail Transaksi (Header + Items) */
    // Note: Ini memerlukan @Transaction dan kelas gabungan (TransactionWithItems)
    // Di sini kita hanya akan menyediakan fungsi Header, Item akan dipanggil terpisah
    // atau menggunakan fungsi Room gabungan (seperti di atas, jika Anda membuatnya)

    // Asumsi: Jika menggunakan TransactionWithItems (gabungan Header+Item)
     @Transaction
     @Query("SELECT * FROM transactions WHERE id = :transactionId")
     suspend fun getTransactionWithItems(transactionId: String): TransactionWithItems?

    @Transaction
    suspend fun recordTransactionAndItems(
        transaction: TransactionEntity,
        items: List<TransactionItemEntity>,
        transactionItemDao: TransactionItemDao
    ) {
        // 1. Insert Header Transaksi (Parent)
        insertTransaction(transaction)

        // 2. Insert Semua Item (Children)
        // Kita harus menggunakan DAO yang berbeda (TransactionItemDao)
        transactionItemDao.insertTransactionItems(items)

        // Catatan: Anda perlu memastikan TransactionItemDao memiliki fungsi insertTransactionItems
        // suspend fun insertTransactionItems(items: List<TransactionItemEntity>)
    }
}
