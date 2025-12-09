package com.praktikum.abstreetfood_management.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap // Import Kritis
import androidx.lifecycle.viewModelScope
import com.praktikum.abstreetfood_management.data.repository.DashboardRepository
import com.praktikum.abstreetfood_management.domain.model.DailyMetric // Asumsi model ini sudah ada
import com.praktikum.abstreetfood_management.domain.model.TopSellingProduct
import com.praktikum.abstreetfood_management.domain.usecase.SessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: DashboardRepository,
    private val sessionUseCase: SessionUseCase,
) : ViewModel() {

    // --- State Sesi User ---
    private val _currentUserId = MutableLiveData<String?>()
    val currentUserId: LiveData<String?> = _currentUserId

    private val _currentUserRole = MutableLiveData<String?>()
    val currentUserRole: LiveData<String?> = _currentUserRole

    // --- Properti Waktu ---
    private val startOfDay: Long
        get() {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            // Log ini akan dieksekusi setiap kali properti diakses
            Log.d("VIEWMODEL_TIME", "Today Start: ${calendar.timeInMillis}")
            return calendar.timeInMillis
        }

    private val startOfYesterday: Long
        get() {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            Log.d("VIEWMODEL_TIME", "Yesterday Start: ${calendar.timeInMillis}")
            return calendar.timeInMillis
        }

    // --- LiveData Metrik Perbandingan (Hari Ini vs. Kemarin) ---

    val dailyRevenueMetrics: LiveData<DailyMetric> = _currentUserId.switchMap { userId ->
        if (userId.isNullOrEmpty()) {
            MutableLiveData(DailyMetric(0.0, 0.0, 0.0))
        } else {
            // Ambil data dari Repository dan tambahkan log di ViewModel
            repository.getRevenueMetrics(userId, startOfDay, startOfYesterday).asLiveData().map { metrics ->
                Log.d("VIEWMODEL_METRIC", "Revenue Received: ${"%.2f".format(metrics.currentTotal)} (${"%.2f".format(metrics.percentageChange)}%)")
                metrics
            }
        }
    }

    val dailyTransactionMetrics: LiveData<DailyMetric> = _currentUserId.switchMap { userId ->
        if (userId.isNullOrEmpty()) {
            MutableLiveData(DailyMetric(0.0, 0.0, 0.0))
        } else {
            // Ambil data dari Repository dan tambahkan log di ViewModel
            repository.getTransactionMetrics(userId, startOfDay, startOfYesterday).asLiveData().map { metrics ->
                Log.d("VIEWMODEL_METRIC", "Transaction Count Received: ${metrics.currentTotal.toInt()} (${"%.2f".format(metrics.percentageChange)}%)")
                metrics
            }
        }
    }

    // --- Data Live Lainnya ---

    // Total Pendapatan Harian Hari Ini (Versi Lama, dapat dipertahankan untuk tampilan spesifik)
    val dailyRevenue: LiveData<Double?> = _currentUserId.switchMap { userId ->
        if (userId.isNullOrEmpty()) MutableLiveData(0.0)
        else repository.getDailyRevenue(userId, startOfDay).asLiveData()
    }

    // Total Transaksi Harian Hari Ini (Versi Lama)
    val dailyTransactionCount: LiveData<Int?> = _currentUserId.switchMap { userId ->
        if (userId.isNullOrEmpty()) MutableLiveData(0)
        else repository.getDailyTransactionCount(userId, startOfDay).asLiveData()
    }

    // Produk Terlaris
    val topSellingProducts: LiveData<List<TopSellingProduct>> = repository.getTopSellingProducts().asLiveData()

    // Semua Produk
    val allProducts = repository.getAllProducts().asLiveData()


    // --- Inisialisasi ---
    init {
        viewModelScope.launch {
            sessionUseCase.getCurrentUserId().collect { id ->
                _currentUserId.value = id
                Log.d("VIEWMODEL_USER", "User ID Set: $id")
            }
            sessionUseCase.getCurrentUserRole().collect { role -> _currentUserRole.value = role }
        }
        // loadDailyMetrics() // Dihapus karena switchMap menangani pemuatan otomatis saat _currentUserId berubah.
    }
}