package com.praktikum.abstreetfood_management.domain.usecase

import android.util.Log
import com.praktikum.abstreetfood_management.data.repository.ReportRepository // Asumsi Anda punya ini
import com.praktikum.abstreetfood_management.domain.model.SaleReportItem
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportUseCase @Inject constructor(
    private val reportRepository: ReportRepository,
    private val csvConverter: CsvConverter
) {
    suspend fun getSalesDataForPeriod(period: ReportPeriod): List<SaleReportItem> {
        val (start, end) = getTimeRange(period)
        return reportRepository.getDetailedSales(start, end)
    }

    fun generateCsvString(data: List<SaleReportItem>): String {
        return csvConverter.convertSalesToCsv(data)
    }

    /**
     * Menghitung rentang waktu (startTime dan endTime) dalam format Long (Timestamp).
     */
//    private fun calculateTimeRange(period: ReportPeriod): Pair<Long, Long> {
//        val calendar = Calendar.getInstance()
//        val endTime = calendar.timeInMillis // Sekarang
//
//        when (period) {
//            ReportPeriod.TODAY -> {
//                calendar.set(Calendar.HOUR_OF_DAY, 0)
//                calendar.set(Calendar.MINUTE, 0)
//                calendar.set(Calendar.SECOND, 0)
//                calendar.set(Calendar.MILLISECOND, 0)
//            }
//            ReportPeriod.LAST_7_DAYS -> {
//                calendar.add(Calendar.DAY_OF_YEAR, -7)
//            }
//            ReportPeriod.LAST_30_DAYS -> {
//                calendar.add(Calendar.DAY_OF_YEAR, -30)
//            }
//        }
//        val startTime = calendar.timeInMillis
//        return Pair(startTime, endTime)
//    }
    /**
     * ✅ FUNGSI BARU/KOREKSI: Mengambil rentang waktu dan JUMLAH HARI.
     */
    fun getTimeRange(period: ReportPeriod): Triple<Long, Long, Int> {
        val LOG_TAG = "RUC_TIME"

//        val calendar = Calendar.getInstance()
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val endTime = calendar.timeInMillis // Sekarang
        var daysCount: Int = 1 // Default untuk TODAY

        when (period) {
            ReportPeriod.TODAY -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                // daysCount tetap 1
            }
            ReportPeriod.LAST_7_DAYS -> {
                calendar.add(Calendar.DAY_OF_YEAR, -6)
                daysCount = 7
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
            ReportPeriod.LAST_30_DAYS -> {
                calendar.add(Calendar.DAY_OF_YEAR, -29)
                daysCount = 30
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
        }
        val startTime = calendar.timeInMillis
        // ✅ Mengembalikan Tiga Nilai: Start, End, Count
        Log.d(LOG_TAG, "Range for $period: Start=$startTime, End=$endTime, Days=$daysCount")
        return Triple(startTime, endTime, daysCount)
    }
}

enum class ReportPeriod { TODAY, LAST_7_DAYS, LAST_30_DAYS }