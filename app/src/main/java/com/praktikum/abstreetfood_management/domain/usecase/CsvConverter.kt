package com.praktikum.abstreetfood_management.domain.usecase

import com.praktikum.abstreetfood_management.domain.model.SaleReportItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CsvConverter @Inject constructor() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

    /**
     * Mengkonversi List<SaleReportItem> menjadi String format CSV.
     * @param data List data laporan penjualan rinci.
     * @return String dalam format CSV.
     */
    fun convertSalesToCsv(data: List<SaleReportItem>): String {
        // 1. Definisikan Header Kolom
        val header = "Transaction ID,Waktu Transaksi,Nama Produk,Kuantitas,Harga Item,Total Revenue Item,ID Outlet\n"

        // 2. Konversi Data Baris
        val rows = data.joinToString("\n") { item ->
            val timeString = dateFormat.format(Date(item.transactionTime))

            // Menggunakan koma sebagai pemisah (separator)
            val rowData = listOf(
                item.transactionId,
                timeString,
                item.productName.replace(",", ""), // Bersihkan koma dalam nama produk agar tidak merusak format CSV
                item.quantity.toString(),
                item.itemPrice.toString(),
                item.totalItemRevenue.toString(),
                item.outletId
            )
            rowData.joinToString(",")
        }

        return header + rows
    }
}