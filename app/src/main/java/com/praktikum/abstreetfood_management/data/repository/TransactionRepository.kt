package com.praktikum.abstreetfood_management.data.repository

import android.annotation.SuppressLint
import android.util.Log
import com.praktikum.abstreetfood_management.data.local.dao.ShiftDao
import com.praktikum.abstreetfood_management.data.local.dao.TransactionDao
import com.praktikum.abstreetfood_management.data.local.dao.TransactionItemDao
import com.praktikum.abstreetfood_management.data.local.entity.ShiftEntity
import com.praktikum.abstreetfood_management.data.local.entity.TransactionEntity
import com.praktikum.abstreetfood_management.data.local.entity.TransactionItemEntity
import com.praktikum.abstreetfood_management.data.mapper.toDomain
import com.praktikum.abstreetfood_management.data.mapper.toDomainNewItem
import com.praktikum.abstreetfood_management.data.mapper.toEntity
import com.praktikum.abstreetfood_management.data.network.ApiService
import com.praktikum.abstreetfood_management.domain.model.NewTransaction
import com.praktikum.abstreetfood_management.domain.model.NewTransactionItem
import com.praktikum.abstreetfood_management.domain.model.Transaction
import com.praktikum.abstreetfood_management.domain.model.TransactionDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val transactionItemDao: TransactionItemDao,
    private val shiftDao: ShiftDao,
    private val apiService: ApiService // Untuk Push Sync
) : ITransactionRepository {

    private val TAG = "TRANSACTION_REPO"
    private val DETAIL_TAG = "TRANSACTION_DETAIL"

    // --- Transaction (LOKAL) ---

//    @SuppressLint("TimberArgCount")
//    override suspend fun recordNewTransaction(newTransaction: NewTransaction): Result<Unit> {
    override suspend fun recordNewTransaction(newTransaction: NewTransaction): Result<String> {
        return try {
            val transactionId = UUID.randomUUID().toString()

            // 1. Buat Entitas Transaksi Lokal (isSynced = false)
            val transactionEntity = TransactionEntity(
                id = transactionId,
                userId = newTransaction.userId,
                outletId = newTransaction.outletId,
                subTotal = newTransaction.subTotal,
                grandTotal = newTransaction.grandTotal,
                note = newTransaction.note,
                transactionTime = newTransaction.transactionTime,
                isSynced = false
            )
//            transactionDao.insertTransaction(transactionEntity)
            val itemEntities = newTransaction.items.map { item ->
                item.toEntity(transactionId) // Menggunakan mapper yang sudah ada
            }

            transactionDao.recordTransactionAndItems(
//                transactionId,
//                newTransaction,
//                newTransaction.items
                transactionEntity,
                itemEntities,
                transactionItemDao
            )


            Log.d(DETAIL_TAG, "--- NEW TRANSACTION LOG ---")
            Log.d(DETAIL_TAG, "Transaction ID: $transactionId")
            Log.d(DETAIL_TAG, "User ID: ${newTransaction.userId}")
            Log.d(DETAIL_TAG, "Outlet ID: ${newTransaction.outletId}")
            Log.d(DETAIL_TAG, "Transaction Time (ms): ${newTransaction.transactionTime}")
            Log.d(DETAIL_TAG, "SubTotal: ${newTransaction.subTotal}")
            Log.d(DETAIL_TAG, "GrandTotal: ${newTransaction.grandTotal}")
            Log.d(DETAIL_TAG, "Item Count: ${newTransaction.items.size}")
            // Log detail setiap item
            newTransaction.items.forEachIndexed { index, item ->
                Log.d(DETAIL_TAG,
                    "Item ${index + 1}: " +
                            "| ID=${item.productItemId} " +
                            "| Name=${item.productName} " +
                            "| Variant=${item.variantName} " +
                            "| Qty=${item.quantity} " +
                            "| Price@=${item.itemPrice}"
                )
            }
           Log.d(DETAIL_TAG, "--- END LOG ---")
            // 2. Buat Entitas Item Transaksi Lokal
//            val itemEntities = newTransaction.items.map { item ->
////                TransactionItemEntity(
////                    transactionId = transactionId,
////                    productItemId = item.productItemId,
////                    quantity = item.quantity,
////                    itemPrice = item.itemPrice,
////                    isSynced = false
////                )
//                item.toEntity(transactionId)
//            }
            // Asumsi TransactionItemDao punya fungsi insertList
//             transactionItemDao.insertTransactionItems(itemEntities)

            // 3. (OPSIONAL): Kurangi stok bahan baku (logic RIRA) di sini.

//            Result.success(Unit)
            Result.success(transactionId)
        } catch (e: Exception) {
            Timber.e(e, "TRANSACTION_REPO: Failed to record new transaction.")
            Result.failure(e)
        }
    }

    /** Mendapatkan daftar transaksi terakhir untuk History/Laporan */
    override fun getTransactionHistory(): Flow<List<Transaction>> {

        return transactionDao.getAllTransactions().map { entities ->
            entities.map { it.toDomain() }
        }.onEach { transactions -> // ✅ LOGGING DITAMBAHKAN DI SINI
            if (transactions.isEmpty()) {
                // Log jika daftar kosong
                Log.d("HISTORY_DATA", "Riwayat Transaksi: Kosong")
            } else {
                // Log header dan detail setiap transaksi
                Log.d("HISTORY_DATA", "--- START RIWAYAT TRANSAKSI (${transactions.size} total) ---")
                transactions.forEach { tx ->
                    // Memotong ID agar log tidak terlalu panjang
                    val shortId = tx.id.take(8).uppercase(Locale.ROOT)
                    val transactionTime = SimpleDateFormat("dd/MM HH:mm", Locale("in", "ID")).format(
                        Date(tx.transactionTime))

                    Log.d("HISTORY_DATA", "ID: $shortId | Total: Rp${tx.grandTotal} | Waktu: $transactionTime | User: ${tx.userId.take(4)}")
                }
                Log.d("HISTORY_DATA", "--- END RIWAYAT TRANSAKSI ---")
            }
        }
    }

    /** Mendapatkan detail transaksi lengkap untuk Cetak Nota (Header + Items) */
//    override suspend fun getTransactionDetail(transactionId: String): TransactionDetail? {
//        val headerEntity = transactionDao.getTransactionById(transactionId)
////        val itemEntities = transactionItemDao.getItemsByTransactionId(transactionId) // Panggil Item
//        val itemsWithProduct = transactionItemDao.getItemsDetailByTransactionId(transactionId)
//
//        if (headerEntity == null) return null
//
//        // Asumsi Anda memiliki mapper:
//        val header = headerEntity.toDomain()
//
//        // Item Entities harus dimapping ke NewTransactionItem atau model yang sesuai untuk tampilan nota
////        val items = itemEntities.map { itemEntity ->
////            // Ini memerlukan data nama produk dari ProductRepository, yang idealnya di-join di Room
////            // Untuk saat ini, kita mapping sederhana:
////            NewTransactionItem(
////                productItemId = itemEntity.productItemId,
////                productName = "Produk Unknown", // HARUS DI-JOIN/DIPANGGIL DARI ProductRepository
////                variantName = "Varian Unknown", // HARUS DI-JOIN/DIPANGGIL DARI ProductRepository
////                quantity = itemEntity.quantity,
////                itemPrice = itemEntity.itemPrice
////            )
////        }
//
//        // Map hasil JOIN ke model domain (NewTransactionItem)
//        val items = itemsWithProduct.map { itemDetail ->
//            // TIDAK PERLU lagi hardcode "Produk Unknown"
//            NewTransactionItem(
//                productItemId = itemDetail.productItemId,
//                productName = itemDetail.productName,
//                variantName = itemDetail.variantName, // Menggunakan data JOIN
//                quantity = itemDetail.quantity,
//                itemPrice = itemDetail.itemPrice
//            )
//        }
//
//        return TransactionDetail(header = header, items = items)
//    }

    override suspend fun getTransactionDetailById(transactionId: String): TransactionDetail? {

        // 1. Ambil Header Transaksi
        val headerEntity = transactionDao.getTransactionById(transactionId)

        if (headerEntity == null) {
            return null
        }

        // 2. Ambil Item Detail HASIL JOIN (Menggunakan fungsi DAO yang baru)
        // ASUMSI: transactionItemDao memiliki getItemsDetailByTransactionId
        val itemsWithProduct = transactionItemDao.getItemsDetailByTransactionId(transactionId)

        // 3. Mapping Header
        val header = headerEntity.toDomain()

        // 4. Mapping Item Detail Hasil JOIN ke Domain
        val items = itemsWithProduct.map { itemDetail ->
            itemDetail.toDomainNewItem()
        }

        // 5. Gabungkan
        return TransactionDetail(header = header, items = items)
    }

//    override fun getDailyRevenue(userId: String): Flow<Double?> {
//        // Asumsi logic waktu ada di ViewModel/UseCase
//        val startOfDay = System.currentTimeMillis() - (24 * 60 * 60 * 1000) // Contoh
//        return transactionDao.getDailyRevenue(userId, startOfDay)
//    }

    override fun getDailyTransactionCount(userId: String): Flow<Int?> {
        val startOfDay = System.currentTimeMillis() - (24 * 60 * 60 * 1000) // Contoh
        return transactionDao.getDailyTransactionCount(userId, startOfDay)
    }

    /** Mendapatkan detail Transaksi berdasarkan ID */
    override suspend fun getTransactionById(transactionId: String): Transaction? {
        // Asumsi TransactionDao memiliki fungsi getTransactionWithItems(id: String)
        // yang mengembalikan objek gabungan (Contoh: TransactionWithItems)
        val transactionWithItems = transactionDao.getTransactionWithItems(transactionId)

        // Perlu Mapper: TransactionWithItems -> Domain Model Transaction
        // return transactionWithItems?.toDomain() // Asumsi Mapper sudah ada
        return null // Placeholder
    }

    /** Mengupdate Transaksi (Digunakan untuk Void/Edit) */
    override suspend fun updateTransaction(transaction: TransactionEntity): Result<Unit> {
        return try {
            // Mengupdate entitas transaksi (menggunakan REPLACE)
            transactionDao.insertTransaction(transaction)

            // Perlu ditambahkan logika untuk mengupdate TransactionItemEntity jika ada perubahan item

            // Set isSynced = false agar di-PUSH saat sinkronisasi berikutnya
            val pendingTransaction = transaction.copy(isSynced = false)
            transactionDao.insertTransaction(pendingTransaction)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override fun getDailyRevenueForPeriod(startTime: Long, endTime: Long): Flow<List<DailySalesData>> {
        return transactionDao.getDailyRevenueForPeriod(startTime, endTime)
            .map { rawList ->
                // Tahap Mapping (Transform)
                Log.d("RAW_DATA_CHECK", "Raw List Count: ${rawList.size}")
                rawList.forEach { raw ->
                    Log.d("RAW_DATA_CHECK", "Timestamp: ${raw.dayTimestamp}, Revenue: ${raw.totalRevenue}")
                }
                rawList.map { raw ->
                    DailySalesData(
                        dayTimestamp = raw.dayTimestamp,
                        revenue = raw.totalRevenue
                    )
                }
            }
            .onEach { dailyDataList -> // ✅ LOGGING UNTUK VERIFIKASI DATA
                val logTag = "CHART_DATA_ETL"
                val formatTanggal = SimpleDateFormat("dd-MM", Locale.ROOT)

                if (dailyDataList.isEmpty()) {
                    Log.d(logTag, "Data Pendapatan Harian: Kosong untuk periode ini.")
                } else {
                    Log.d(logTag, "--- START DAILY REVENUE CALCULATION (Total: ${dailyDataList.size} hari) ---")
                    dailyDataList.forEach { data ->
                        val tanggal = formatTanggal.format(Date(data.dayTimestamp))
                        // Log data harian
                        Log.i(logTag, "Tanggal $tanggal | Pendapatan: Rp${data.revenue}")
                    }
                    Log.d(logTag, "--- END DAILY REVENUE CALCULATION ---")
                }
            }
    }
    // ----------------------------------------------------

    // --- Shift Management (LOKAL) ---

    override suspend fun openShift(shift: ShiftEntity) {
        // Shift baru akan memiliki isSynced = false
        shiftDao.openShift(shift)
        // TODO: PUSH SHIFT OPEN KE SERVER di proses Sync
    }

    override suspend fun closeShift(shift: ShiftEntity) {
        // Shift diupdate, tetap isSynced = false
        shiftDao.closeShift(shift)
        // TODO: PUSH SHIFT CLOSE KE SERVER di proses Sync
    }

    override suspend fun getCurrentOpenShift(userId: String): ShiftEntity? {
        return shiftDao.getCurrentOpenShift(userId)
    }


    // --- SYNC (PUSH) ---

    override suspend fun synchronizeTransactions(): Boolean {
        // 1. Ambil Transaksi & Item yang Belum di Sync
        val pendingTransactions = transactionDao.getPendingSyncTransactions() //

        if (pendingTransactions.isEmpty()) {
            return true // Tidak ada yang perlu disync
        }

        // 2. Map ke DTO dan Kirim ke Turso (Asumsi ada fungsi di TursoService)
        // val syncResult = tursoService.pushTransactions(pendingTransactions.map { it.toDto() })

        // 3. Update Status Sync Lokal jika Sukses
        // if (syncResult.isSuccess) {
        //     pendingTransactions.forEach {
        //         val syncedEntity = it.copy(isSynced = true, syncTimestamp = System.currentTimeMillis())
        //         transactionDao.insertTransaction(syncedEntity) // Pakai REPLACE
        //     }
        // }

        // return syncResult.isSuccess
        return false
    }
}