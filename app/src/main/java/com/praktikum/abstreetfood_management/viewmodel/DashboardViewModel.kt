package com.praktikum.abstreetfood_management.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap // <-- Import Kritis
import androidx.lifecycle.viewModelScope
import com.praktikum.abstreetfood_management.data.repository.DashboardRepository
import com.praktikum.abstreetfood_management.domain.model.TopSellingProduct
import com.praktikum.abstreetfood_management.domain.usecase.SessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: DashboardRepository,
    private val sessionUseCase: SessionUseCase,
    // private val authPrefs: AuthPreferenceManager
) : ViewModel() {

    // LiveData yang memegang ID pengguna, akan di-update oleh init
    private val _currentUserId = MutableLiveData<String?>()
    val currentUserId: LiveData<String?> = _currentUserId

    private val _currentUserRole = MutableLiveData<String?>()
    val currentUserRole: LiveData<String?> = _currentUserRole

    // Property untuk mendapatkan timestamp awal hari (00:00:00)
    private val startOfDay: Long
        get() {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return calendar.timeInMillis
        }

    init {
        viewModelScope.launch {
            sessionUseCase.getCurrentUserId().collect { id -> _currentUserId.value = id }
            sessionUseCase.getCurrentUserRole().collect { role -> _currentUserRole.value = role }
        }
    }

//    // 1. Total Pendapatan Harian (Menggunakan switchMap)
//    val dailyRevenue: LiveData<Double?> = _currentUserId.switchMap { userId ->
//        if (userId.isNullOrEmpty()) {
//            MutableLiveData(0.0) // Nilai default 0.0 jika ID belum tersedia/null
//        } else {
//            // Panggil Repository hanya jika userId tersedia
//            repository.getDailyRevenue(userId, startOfDay).asLiveData()
//        }
//    }
//
//    // 2. Total Transaksi Harian
//    val dailyTransactionCount: LiveData<Int?> = _currentUserId.switchMap { userId ->
//        if (userId.isNullOrEmpty()) {
//            MutableLiveData(0) // Nilai default 0 jika ID belum tersedia/null
//        } else {
//            // Panggil Repository (Asumsi fungsi sudah ada di Repository)
//            repository.getDailyTransactionCount(userId, startOfDay).asLiveData()
//        }
//    }
//
//    // 3. Produk Terlaris (Top Selling) - Umumnya tidak bergantung pada userId tertentu
//    val topSellingProducts: LiveData<List<TopSellingProduct>> = repository.getTopSellingProducts().asLiveData()

    // 4. Total Stok (Asumsi ini adalah total stok bahan baku/produk jadi)
    // val totalStock = repository.getTotalStock().asLiveData()

    // --- 1. Metrik Dasar (Revenue & Transaksi) ---
    val dailyRevenue: LiveData<Double?> = _currentUserId.switchMap { userId ->
        if (userId.isNullOrEmpty()) MutableLiveData(0.0)
        else repository.getDailyRevenue(userId, startOfDay).asLiveData()
    }

    val dailyTransactionCount: LiveData<Int?> = _currentUserId.switchMap { userId ->
        if (userId.isNullOrEmpty()) MutableLiveData(0)
        else repository.getDailyTransactionCount(userId, startOfDay).asLiveData()
    }

    // --- 2. Data Produk & Stok (Untuk Tampilan Dinamis) ---

    // Produk Terlaris (Tab Penjualan Teratas / Default)
    val topSellingProducts: LiveData<List<TopSellingProduct>> = repository.getTopSellingProducts().asLiveData()

    // Semua Produk (Untuk Tampilan "All Products" di Tab Penjualan)
    val allProducts = repository.getAllProducts().asLiveData()

//    // Semua Stok Bahan (Data sumber yang akan difilter)
//    private val allStockItemsFlow = repository.getAllStockItems()
//
//    // Stok Kritis (Difilter dari semua stok, isCritical = true jika currentStock < minThreshold)
//    val warningStockItems: LiveData<List<StockItemEntity>> = allStockItemsFlow.map { items ->
//        items.filter { it.currentStock < it.minStockThreshold } // Asumsi StockItemEntity memiliki kolom minStockThreshold
//    }.asLiveData()
//
//    // --- 3. Logika Tampilan Stok (StockDisplayMode) ---
//
//    // LiveData untuk mengontrol Tampilan Stok (Warning vs All)
//    private val _stockDisplayMode = MutableLiveData(StockDisplayMode.WARNING)
//    val stockDisplayMode: LiveData<StockDisplayMode> = _stockDisplayMode
//
//    /**
//     * Data yang diobservasi oleh RecyclerView saat Tab 'Stok' aktif.
//     * Menggunakan switchMap untuk berpindah sumber data berdasarkan mode.
//     */
//    val currentStockList: LiveData<List<StockItemEntity>> = _stockDisplayMode.switchMap { mode ->
//        when (mode) {
//            StockDisplayMode.WARNING -> warningStockItems // Filtered List (Warning)
//            StockDisplayMode.ALL -> allStockItemsFlow.asLiveData() // Full List (All)
//        }
//    }
//
//    fun setStockDisplayMode(mode: StockDisplayMode) {
//        _stockDisplayMode.value = mode
//    }
}