package com.praktikum.abstreetfood_management.utility

import com.praktikum.abstreetfood_management.data.repository.DailySalesData
import java.util.Calendar

/**
 * Utility class untuk memproses data penjualan harian
 * dengan mengisi hari-hari yang hilang dengan revenue 0.0.
 */
object DailySalesProcessor {

    // Konstanta 1 hari dalam milidetik
    private const val ONE_DAY_MS = 24 * 60 * 60 * 1000L

    /**
     * Mengisi hari-hari yang hilang dalam rentang waktu yang diminta (daysCount).
     * * @param rawData Data yang dikembalikan dari SQL (hanya hari dengan penjualan > 0).
     * @param daysCount Jumlah total hari yang harus di-plot (misalnya, 7 atau 30).
     * @param startTime Timestamp awal rentang (00:00:00 dari hari pertama).
     */
    fun fillMissingDays(rawData: List<DailySalesData>, daysCount: Int, startTime: Long): List<DailySalesData> {

        if (daysCount <= 0 || startTime <= 0) return rawData

        // 1. Buat Map untuk lookup cepat berdasarkan timestamp awal hari
        // Ini lebih cepat daripada looping list
        val dataMap = rawData.associateBy { it.dayTimestamp }
        val filledList = mutableListOf<DailySalesData>()

        var currentDayStartMs = startTime

        // 2. Loop melalui setiap hari dalam rentang yang diminta (7 atau 30 kali)
        for (i in 0 until daysCount) {
            val dayStartTimestamp = currentDayStartMs

            // ✅ Ganti if (data != null) dengan pengecekan map yang eksplisit
            if (dataMap.containsKey(dayStartTimestamp)) {
                // Hari ini ada penjualan
                filledList.add(dataMap.getValue(dayStartTimestamp)) // Ambil nilai yang benar (33000/40000)
            } else {
                // Hari ini tidak ada penjualan (Isi dengan 0.0)
                filledList.add(DailySalesData(
                    dayTimestamp = dayStartTimestamp,
                    revenue = 0.0
                ))
            }
//            val data = dataMap[currentDayStartMs]
//
//            if (data != null) {
//                // Hari ini ada penjualan (gunakan data asli)
//                filledList.add(data)
//            } else {
//                // Hari ini tidak ada penjualan (Isi dengan 0.0)
//                filledList.add(DailySalesData(
//                    dayTimestamp = currentDayStartMs,
//                    revenue = 0.0
//                ))
//            }

            // Pindah ke awal hari berikutnya
            currentDayStartMs += ONE_DAY_MS
        }

        return filledList
    }
}