//package com.praktikum.abstreetfood_management.worker
//
//import android.annotation.SuppressLint
//import android.content.Context
//import androidx.work.Constraints
//import androidx.work.CoroutineWorker // <--- TAMBAHKAN INI
//import androidx.work.ExistingPeriodicWorkPolicy
//import androidx.work.NetworkType
//import androidx.work.PeriodicWorkRequestBuilder
//import androidx.work.WorkerParameters
//import androidx.work.WorkManager
//import com.praktikum.abstreetfood_management.data.local.preferences.SyncPreferenceManager
//import kotlinx.coroutines.flow.first
//import timber.log.Timber
//import java.util.Calendar
//import java.util.concurrent.TimeUnit
//
//// Contoh Logika di SyncWorker.kt
//class SyncWorker(
//    appContext: Context,
//    workerParams: WorkerParameters
//) : CoroutineWorker(appContext, workerParams) {
//
//    // Dapatkan instance SyncPreferenceManager via DI
//    // private val syncPrefsManager: SyncPreferenceManager = get()
//
//    @SuppressLint("TimberArgCount")
//    override suspend fun doWork(): Result {
//        val syncPrefsManager = SyncPreferenceManager(applicationContext) // Ganti ini dengan DI
//
//        // 1. Cek status sync dari DataStore
//        val isSyncEnabled = syncPrefsManager.isSyncEnabledFlow.first()
//
//        if (!isSyncEnabled) {
//            // Sinkronisasi dinonaktifkan oleh pengguna. Batalkan pekerjaan.
//            // Gunakan Result.success() karena Worker berhasil menyelesaikan pengecekan.
//            return Result.success()
//        }
//
//        Timber.d("SYNC_WORKER", "Memulai proses sinkronisasi...")
//
//        // 2. Logika Sinkronisasi:
//        // a) PUSH data transaksi pending ke Turso (TransactionRepository.synchronizeTransactions())
//        // b) PULL data master terbaru (ProductRepository.syncProductAndStockMasterData())
//
//        // Asumsi: if (transactionRepository.synchronizeTransactions() && productRepository.syncMasterData())
//        // { return Result.success() } else { return Result.retry() }
//
//        return Result.success()
//    }
//
//    companion object {
//        const val WORK_NAME = "DailySyncWorker"
//
//        /**
//         * Menjadwalkan SyncWorker untuk berjalan setiap hari pada pukul 22:00 WIB.
//         */
//        fun scheduleDailySync(context: Context) {
//            val constraints = Constraints.Builder()
//                .setRequiredNetworkType(NetworkType.CONNECTED) // Hanya sync saat ada koneksi
//                .build()
//
//            // Hitung delay awal untuk pukul 22:00 hari ini atau besok
//            val now = Calendar.getInstance()
//            val target = Calendar.getInstance().apply {
//                timeInMillis = System.currentTimeMillis()
//                set(Calendar.HOUR_OF_DAY, 22) // Pukul 22 (10 malam)
//                set(Calendar.MINUTE, 0)
//                set(Calendar.SECOND, 0)
//            }
//
//            if (target.before(now)) {
//                // Jika sekarang sudah lewat pukul 22:00, jadwalkan untuk besok
//                target.add(Calendar.DATE, 1)
//            }
//
//            // Hitung waktu tunggu hingga target waktu (dalam milidetik)
//            val initialDelay = target.timeInMillis - now.timeInMillis
//
//            val workRequest = PeriodicWorkRequestBuilder<SyncWorker>(
//                repeatInterval = 1, // Ulangi setiap 24 jam
//                repeatIntervalTimeUnit = TimeUnit.DAYS
//            )
//                .setConstraints(constraints)
//                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS) // Atur delay awal
//                .build()
//
//            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
//                WORK_NAME,
//                ExistingPeriodicWorkPolicy.REPLACE, // Ganti pekerjaan lama jika ada
//                workRequest
//            )
//        }
//    }
//}

package com.praktikum.abstreetfood_management.worker

import android.content.Context
import androidx.work.CoroutineWorker // <--- TAMBAHKAN INI
import androidx.work.WorkerParameters
import com.praktikum.abstreetfood_management.data.local.preferences.SyncPreferenceManager
import kotlinx.coroutines.flow.first

// Contoh Logika di SyncWorker.kt
class SyncWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    // Dapatkan instance SyncPreferenceManager via DI
    // private val syncPrefsManager: SyncPreferenceManager = get()

    override suspend fun doWork(): Result {
        val syncPrefsManager = SyncPreferenceManager(applicationContext) // Ganti ini dengan DI

        // 1. Cek status sync dari DataStore
        val isSyncEnabled = syncPrefsManager.isSyncEnabledFlow.first()

        if (!isSyncEnabled) {
            // Sinkronisasi dinonaktifkan oleh pengguna. Batalkan pekerjaan.
            // Gunakan Result.success() karena Worker berhasil menyelesaikan pengecekan.
            return Result.success()
        }

        // 2. Lanjutkan proses sinkronisasi ke Turso jika diaktifkan
        // val transactionRepository = get<TransactionRepository>()
        // transactionRepository.synchronizeTransactions()

        return Result.success()
    }
}