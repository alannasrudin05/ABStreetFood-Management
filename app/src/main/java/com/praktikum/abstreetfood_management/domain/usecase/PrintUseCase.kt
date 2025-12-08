package com.praktikum.abstreetfood_management.domain.usecase

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.praktikum.abstreetfood_management.data.service.BluetoothPrintService
import com.praktikum.abstreetfood_management.domain.model.TransactionDetail
import javax.inject.Inject

class PrintUseCase @Inject constructor(
    private val bluetoothPrintService: BluetoothPrintService
) {

    // LiveData untuk mengirim hasil scan ke Fragment
    val scanResults: LiveData<List<BluetoothDevice>> =
        bluetoothPrintService.scanResults.asLiveData() // Asumsi service menyediakan Flow/LiveData

    /** Memicu pemindaian perangkat Bluetooth. */
    fun startScan() {
        bluetoothPrintService.startDiscovery()
    }

    /** Membatalkan pemindaian. */
    fun stopScan() {
        bluetoothPrintService.cancelDiscovery()
    }

    /**
     * ✅ Fungsi utama untuk mencetak nota via koneksi Bluetooth langsung (ESC/POS).
     * @param deviceAddress Alamat MAC printer Bluetooth.
     * @param detail Data TransactionDetail lengkap.
     * @return true jika cetak berhasil, false jika koneksi/pengiriman gagal.
     */
    suspend fun printViaBluetooth(deviceAddress: String, detail: TransactionDetail): Boolean {
        // 1. Format data TransactionDetail ke byte ESC/POS
        val escPosData = formatToEscPos(detail)

        // 2. Kirim data melalui koneksi Bluetooth
        return bluetoothPrintService.sendData(deviceAddress, escPosData)
    }

    /**
     * Mengubah model data TransactionDetail menjadi byte array ESC/POS.
     * Ini adalah logika inti formatting thermal printer.
     */
    private fun formatToEscPos(detail: TransactionDetail): ByteArray {
        val bytes = mutableListOf<Byte>()

        // Asumsi format Rupiah sederhana
        val formatRupiah = { value: Double ->
            "Rp " + "%,.0f".format(value).replace(',', '.')
        }

        // --- Header Toko (Center Alignment) ---
        bytes.add(0x1B) // ESC
        bytes.add(0x61) // a
        bytes.add(0x01) // 1 (Center)

        bytes.addAll("ABStreetFood\n".toByteArray().toTypedArray())
//        bytes.addAll("ABStreetFood\n".toByteArray())
        bytes.addAll("Nota ID: ${detail.header.id.take(8)}\n".toByteArray().toTypedArray())
        bytes.addAll("-------------------------------\n".toByteArray().toTypedArray())

        // --- Detail Item (Left Alignment) ---
        bytes.add(0x1B) // ESC
        bytes.add(0x61) // a
        bytes.add(0x00) // 0 (Left)

        // Loop Item
        detail.items.forEach { item ->
            val name = "${item.productName} (${item.variantName})"
            val qtyPrice = "${item.quantity} x ${formatRupiah(item.itemPrice)}"
            val total = formatRupiah(item.quantity * item.itemPrice)

            bytes.addAll("$name\n".toByteArray().toTypedArray())
            bytes.addAll("  $qtyPrice\t\t$total\n".toByteArray().toTypedArray()) // Tab '\t' untuk alignment
        }

        bytes.addAll("-------------------------------\n".toByteArray().toTypedArray())

        // --- Total (Right Alignment) ---
        bytes.add(0x1B) // ESC
        bytes.add(0x61) // a
        bytes.add(0x02) // 2 (Right)

        bytes.addAll("Subtotal: ${formatRupiah(detail.header.subTotal)}\n".toByteArray().toTypedArray())
        bytes.addAll("GRAND TOTAL: ${formatRupiah(detail.header.grandTotal)}\n".toByteArray().toTypedArray())

        // --- Cut Paper Command ---
        bytes.add(0x1D) // GS
        bytes.add(0x56) // V
        bytes.add(0x01) // Full cut

        return bytes.toByteArray()
    }
}