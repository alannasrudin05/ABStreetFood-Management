package com.praktikum.abstreetfood_management

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.praktikum.abstreetfood_management.worker.SyncWorker
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class ABStreetFoodManagement: Application() {
    override fun onCreate() {
        super.onCreate()

        setupRecurringSync()
    }

    private fun setupRecurringSync() {
        // 1. Tentukan batasan (misalnya, hanya saat ada koneksi)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // 2. Buat permintaan kerja berkala (Misalnya, setiap 30 menit)
        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>( // Ini adalah Builder
            repeatInterval = 30, // 30 menit
            repeatIntervalTimeUnit = TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()
        // 3. Jadwalkan pekerjaan.
        // USE_EXISTING memastikan hanya satu worker sync yang berjalan pada satu waktu.
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "TursoSyncWork",
            ExistingPeriodicWorkPolicy.KEEP, // Pertahankan pekerjaan yang ada jika sudah berjalan
            syncRequest
        )
    }
}