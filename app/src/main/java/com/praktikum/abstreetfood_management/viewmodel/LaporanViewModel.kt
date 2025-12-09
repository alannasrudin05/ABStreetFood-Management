package com.praktikum.abstreetfood_management.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.praktikum.abstreetfood_management.data.repository.DailySalesData
import com.praktikum.abstreetfood_management.data.repository.ITransactionRepository
import com.praktikum.abstreetfood_management.domain.model.SaleReportItem
import com.praktikum.abstreetfood_management.domain.usecase.ReportPeriod
import com.praktikum.abstreetfood_management.domain.usecase.ReportUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import javax.inject.Inject
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flatMapLatest
import androidx.lifecycle.asLiveData
import com.praktikum.abstreetfood_management.utility.DailySalesProcessor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class LaporanViewModel @Inject constructor(
    private val reportUseCase: ReportUseCase,
    private val transactionRepository: ITransactionRepository
) : ViewModel() {

    private val LOG_TAG = "LVM_FLOW"

    private val _periodInput = MutableStateFlow(ReportPeriod.TODAY)
    val periodInput: StateFlow<ReportPeriod> = _periodInput.asStateFlow()

    // Data yang ditampilkan di Preview Laporan (RecyclerView)
    private val _salesReportData = MutableLiveData<List<SaleReportItem>>()
    val salesReportData: LiveData<List<SaleReportItem>> = _salesReportData

    // Data Grafik (Pendapatan Harian) - Dibuat sebagai LiveData stream
//    private val _dailySalesData = MutableLiveData<List<DailySalesData>>()
//    val dailySalesData: LiveData<List<DailySalesData>> = _dailySalesData

    // Status untuk memberitahu Fragment hasil Ekspor CSV
    private val _csvExportStatus = MutableLiveData<ExportStatus>()
    val csvExportStatus: LiveData<ExportStatus> = _csvExportStatus

    private var currentPeriod: ReportPeriod = ReportPeriod.TODAY // Simpan periode aktif


    val dailySalesData: LiveData<List<DailySalesData>> = _periodInput
        .map { period ->
            val result = reportUseCase.getTimeRange(period)
            Log.d(LOG_TAG, "3. Time Range Calculated (Days: ${result.third}).")
            result
        } // Ambil rentang waktu baru
        .flatMapLatest { (startTime, endTime, daysCount) ->
            // FlatMapLatest akan membatalkan query lama dan memulai query baru dengan rentang waktu yang baru
            Log.d(LOG_TAG, "4. Query DB: Fetching revenue from $startTime to $endTime.") // Log 4
            transactionRepository.getDailyRevenueForPeriod(startTime, endTime)
                // 3. Proses Fill Missing Days
                .map { rawData ->
                    Log.d(LOG_TAG, "5a. Raw DB Data Received (Found: ${rawData.size}). Starting Fill Logic.") // Log 5a

                    val filledData = DailySalesProcessor.fillMissingDays(rawData, daysCount, startTime)

                    Log.d(LOG_TAG, "5b. Fill Logic Complete. Final Count: ${filledData.size}.") // Log 5b
                    filledData
                }
        }
        .onEach { finalData ->
            val logTag = "FINAL_REVENUE_CHECK"
            val formatTanggal = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)

            if (finalData.isNotEmpty()) {
                Log.d(logTag, "--- START FINAL CHART DATA (${finalData.size} days) ---")
                finalData.forEachIndexed { index, data ->
                    val tanggal = formatTanggal.format(Date(data.dayTimestamp))

                    // Log data pendapatan aktual per hari
                    Log.i(logTag,
                        "FINAL DATA [${index + 1}]: Tgl $tanggal | Revenue: Rp${data.revenue}"
                    )
                }
                Log.d(logTag, "--- END FINAL CHART DATA ---")
            } else {
                Log.d(logTag, "FINAL DATA: Data set kosong.")
            }
        }
        .asLiveData(viewModelScope.coroutineContext) // Konversi ke LiveData

    init {
        // Ambil data default saat ViewModel dibuat
//        fetchDailySalesData(currentPeriod)
        fetchReport(ReportPeriod.TODAY)
    }

    /**
     * Mengambil data laporan penjualan berdasarkan periode yang dipilih.
     */
    fun fetchReport(period: ReportPeriod) {
        currentPeriod = period
        Log.d(LOG_TAG, "1. UI Event: fetchReport($period) dipanggil.") // Log 1
        viewModelScope.launch {
            try {
                // 1. Ambil data untuk preview tabel
//                val data = reportUseCase.getSalesDataForPeriod(period)
                val data = withContext(Dispatchers.IO) {
                    reportUseCase.getSalesDataForPeriod(period)
                }
                _salesReportData.value = data
                Log.d(LOG_TAG, "2. Flow Trigger: Mengatur _periodInput = $period.")

                // 2. Refresh data grafik (karena periodenya sama)
//                fetchDailySalesData(period)
                // 2. ✅ Memicu perubahan di Flow (trigger update chart)
                _periodInput.value = period

            } catch (e: Exception) {
                _salesReportData.value = emptyList()
                Log.e(LOG_TAG, "Error fetching report: ${e.message}")
                // Log.e("LaporanViewModel", "Error fetching report: ${e.message}")
            }
        }
    }

//    fun fetchDailySalesData(period: ReportPeriod) {
//        viewModelScope.launch {
//            // 1. Tentukan rentang waktu (startTime, endTime, daysCount)
//            val (startTime, endTime, daysCount) = reportUseCase.getTimeRange(period)
//
//            // 2. Ambil Flow dari Repository
//            transactionRepository.getDailyRevenueForPeriod(startTime, endTime)
//                .collect { rawData ->
//
//                    // ✅ KRITIS: Panggil helper untuk mengisi hari kosong
//                    val filledData = DailySalesProcessor.fillMissingDays(rawData, daysCount, startTime)
//
//                    val logTag = "CHART_DATA_FINAL"
//                    val formatTanggal = SimpleDateFormat("dd-MM", Locale.ROOT)
//
//                    Log.d(logTag, "--- START CHART DATA (${period.name}) ---")
//                    Log.d(logTag, "Periode yang diminta: $daysCount Hari")
//
//                    if (filledData.isEmpty()) {
//                        Log.d(logTag, "Data akhir: Kosong.")
//                    } else {
//                        filledData.forEachIndexed { index, data ->
//                            val tanggal = formatTanggal.format(Date(data.dayTimestamp))
//                            // Log setiap hari, termasuk hari dengan 0.0
//                            Log.i(logTag,
//                                "Day ${index + 1}: Tgl $tanggal | Revenue: Rp${data.revenue}"
//                            )
//                        }
//                    }
//                    Log.d(logTag, "--- END CHART DATA ---")
//
//                    _dailySalesData.postValue(filledData)
//                }
//        }
//    }

    /**
     * ✅ FUNGSI BARU: Mengambil data ringkasan harian untuk LineChart.
     */
//    fun fetchDailySalesData(period: ReportPeriod) {
//        viewModelScope.launch {
//            // Tentukan rentang waktu (startTime dan endTime) berdasarkan periode
//            val (startTime, endTime, daysCount) = reportUseCase.getTimeRange(period)
//
//            // ⚠️ Ambil Flow dari Repository dan kumpulkan data
//            transactionRepository.getDailyRevenueForPeriod(startTime, endTime)
//                .collect { rawData ->
//                    // ✅ PENTING: Isi hari yang kosong dengan 0.0 (Jika Anda menggunakan helper)
//                    // Karena kita tidak memiliki helper fillMissingDays, kita gunakan data mentah dulu.
//                    // Jika Anda sudah membuat helper, panggil di sini:
//                    // val filledData = fillMissingDays(rawData, daysCount)
//
//                    _dailySalesData.postValue(rawData)
//                }
//        }
//    }

    /**
     * Membuat file CSV dari data laporan dan menyimpannya.
     *
     * CATATAN KRITIS: Di Android modern (API 29+), penyimpanan file ke lokasi publik
     * memerlukan penggunaan MediaStore atau Storage Access Framework (SAF)
     * yang harus dipicu di Fragment/Activity. Logika penyimpanan ini adalah contoh dasar.
     * Anda perlu mengganti `getPublicDownloadsDir()` dengan fungsi yang aman SAF/MediaStore.
     */
    fun exportSalesData(period: ReportPeriod) {
        viewModelScope.launch {
            _csvExportStatus.value = ExportStatus.Loading
            try {
                // 1. Ambil data dari Room (Use Case akan menghitung filter waktu)
//                val dataToExport = reportUseCase.getSalesDataForPeriod(period)

                val dataToExport = withContext(Dispatchers.IO) { // <-- Pindahkan ke sini
                    reportUseCase.getSalesDataForPeriod(period)
                }
                if (dataToExport.isEmpty()) {
                    _csvExportStatus.value = ExportStatus.Error("Tidak ada data transaksi di periode ini.")
                    return@launch
                }

                // 2. Konversi ke String CSV
                val csvString = reportUseCase.generateCsvString(dataToExport)

                // 3. Simpan String ke File (Operasi I/O harus di Dispatchers.IO)
                val fileName = "ABStreetFood_Laporan_${period.name}_${System.currentTimeMillis()}.csv"

                withContext(Dispatchers.IO) {
                    val file = File(getTemporaryCacheDir(), fileName) // Simpan sementara di Cache
                    val writer = FileWriter(file)
                    writer.write(csvString)
                    writer.close()

                    // Sukses, kirim path file
                    _csvExportStatus.postValue(ExportStatus.Success(file.absolutePath))
                }
            } catch (e: Exception) {
                _csvExportStatus.postValue(ExportStatus.Error("Gagal menyimpan file CSV: ${e.message}"))
            }
        }
    }

    // TODO: Ganti ini dengan cara yang benar untuk mendapatkan direktori (seperti menggunakan context/application)
    // Untuk tujuan testing dan menghindari error izin I/O di ViewModel:
    private fun getTemporaryCacheDir(): File {
        // Harusnya dipanggil dari context, tapi sebagai placeholder:
        return File(System.getProperty("java.io.tmpdir") ?: "/tmp")
    }
}

// Sealed Class untuk menangani Status Ekspor
sealed class ExportStatus {
    object Loading : ExportStatus()
    data class Success(val filePath: String) : ExportStatus()
    data class Error(val message: String) : ExportStatus()
}