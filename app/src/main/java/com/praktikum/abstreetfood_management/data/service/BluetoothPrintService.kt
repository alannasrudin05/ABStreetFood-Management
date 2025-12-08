package com.praktikum.abstreetfood_management.data.service

import android.Manifest // Wajib
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager // Wajib
import android.os.Build
import androidx.core.content.ContextCompat // Wajib
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Service untuk menangani koneksi dan pengiriman data Bluetooth (Low-Level ESC/POS).
 */
@SuppressLint("MissingPermission") // Anotasi ini tetap diperlukan di tingkat kelas karena kita tidak dapat menjamin izin di sini.
class BluetoothPrintService @Inject constructor(
    private val context: Context // Wajib: Context untuk registerReceiver
) {

    private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    private val _scanResults = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val scanResults: StateFlow<List<BluetoothDevice>> = _scanResults.asStateFlow()

    // =================================================================================
    // 🔑 PERMISSION HELPER (Guard)
    // =================================================================================

    /**
     * Memverifikasi izin Bluetooth/Lokasi yang diperlukan untuk API 31+.
     * Note: Izin harus sudah diminta dan diberikan di Fragment.
     */
    private fun hasRequiredPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
        } else {
            // Untuk API < 31, Lokasi dianggap mencukupi untuk Scan
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }
    }


    // =================================================================================
    // BROADCAST RECEIVER (Scan Logic)
    // =================================================================================

    private val discoveryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        // ✅ SAFETY CHECK: Menggunakan hasRequiredPermissions
                        if (!hasRequiredPermissions()) return

                        // Gunakan nama perangkat yang aman
                        val deviceName = it.name ?: "Unnamed Device"

                        if (it.bondState != BluetoothDevice.BOND_BONDED) {
                            val currentList = _scanResults.value.toMutableList()
                            // Cek berdasarkan address untuk menghindari duplikat
                            if (!currentList.any { d -> d.address == it.address }) {
                                currentList.add(it)
                                _scanResults.value = currentList.toList()
                                Timber.d("Bluetooth Service: Found device: $deviceName")
                            }
                        }
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    Timber.i("Bluetooth Service: Discovery started.")
                    _scanResults.value = emptyList()
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    Timber.i("Bluetooth Service: Discovery finished.")
                }
            }
        }
    }

    // =================================================================================
    // INIT & DISCOVERY FUNCTIONS
    // =================================================================================

    init {
        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }
        // ✅ Register Receiver
        context.registerReceiver(discoveryReceiver, filter)

        // Tambahkan perangkat yang sudah dipairing (terikat) secara default
        if (hasRequiredPermissions()) {
            bluetoothAdapter?.bondedDevices?.let { bondedDevices ->
                _scanResults.value = bondedDevices.toList()
            }
        }
    }

    fun startDiscovery() {
        if (!hasRequiredPermissions()) {
            Timber.e("Bluetooth Service: Izin belum diberikan untuk startDiscovery.")
            return
        }
        if (bluetoothAdapter?.isDiscovering == true) {
            bluetoothAdapter.cancelDiscovery()
        }
        bluetoothAdapter?.startDiscovery()
    }

    fun cancelDiscovery() {
        if (!hasRequiredPermissions()) return
        bluetoothAdapter?.cancelDiscovery()
    }

    // =================================================================================
    // SEND DATA (Koneksi & Kirim)
    // =================================================================================

    /**
     * Mengirim data ESC/POS ke printer Bluetooth.
     * @param deviceAddress Alamat MAC printer.
     * @param data Byte array data ESC/POS.
     * @return true jika berhasil terhubung dan mengirim data.
     */
    suspend fun sendData(deviceAddress: String, data: ByteArray): Boolean = withContext(Dispatchers.IO) {
        // ✅ GUARD CHECK: Cek izin di awal fungsi suspend
        if (!hasRequiredPermissions()) {
            Timber.e("Bluetooth Service: Izin belum diberikan untuk sendData.")
            return@withContext false
        }

        var socket: BluetoothSocket? = null

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            Timber.e("Bluetooth Service: Bluetooth tidak tersedia atau tidak aktif.")
            return@withContext false
        }

        try {
            // Mengambil perangkat remote (Membutuhkan BLUETOOTH_CONNECT)
            val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress)

            // 1. Buat socket koneksi SPP
            socket = device.createRfcommSocketToServiceRecord(SPP_UUID)

            // 2. Batalkan pemindaian yang mungkin aktif
            bluetoothAdapter.cancelDiscovery()

            // 3. Sambungkan socket (Blocking call)
            socket.connect()
            Timber.i("Bluetooth Service: Koneksi berhasil ke $deviceAddress.")

            // 4. Kirim data
            val outputStream = socket.outputStream
            outputStream.write(data)
            outputStream.flush()

            Thread.sleep(500)

            return@withContext true

        } catch (e: SecurityException) {
            // Menangani SecurityException (jika izin dicabut saat runtime)
            Timber.e(e, "Bluetooth Service: Izin tidak tersedia saat mencoba koneksi atau akses data.")
            return@withContext false
        } catch (e: IOException) {
            Timber.e(e, "Bluetooth Service: Gagal terhubung atau mengirim data ke $deviceAddress.")

            try {
                socket?.close()
            } catch (closeException: IOException) {
                Timber.e(closeException, "Bluetooth Service: Gagal menutup socket.")
            }
            return@withContext false
        }
    }
}



//package com.praktikum.abstreetfood_management.data.service
//
//import android.annotation.SuppressLint
//import android.bluetooth.BluetoothAdapter
//import android.bluetooth.BluetoothDevice
//import android.bluetooth.BluetoothSocket
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.content.IntentFilter
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import timber.log.Timber
//import java.io.IOException
//import java.util.UUID
//import javax.inject.Inject
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//
///**
// * Service untuk menangani koneksi dan pengiriman data Bluetooth (Low-Level ESC/POS).
// */
//@SuppressLint("MissingPermission")
//class BluetoothPrintService @Inject constructor(
//    private val context: Context
//) {
//
//    // UUID standar untuk Serial Port Profile (SPP) yang digunakan oleh printer thermal
//    private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
//
//    // Mendapatkan Bluetooth Adapter utama
//    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
//
//    // State untuk mengirim hasil scan ke Use Case/ViewModel
//    private val _scanResults = MutableStateFlow<List<BluetoothDevice>>(emptyList())
//    val scanResults: StateFlow<List<BluetoothDevice>> = _scanResults.asStateFlow()
//
//    // Receiver untuk mendengarkan hasil penemuan perangkat
//    private val discoveryReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            when (intent.action) {
//                BluetoothDevice.ACTION_FOUND -> {
//                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
//                    device?.let {
//                        if (!it.name.isNullOrEmpty() && it.bondState != BluetoothDevice.BOND_BONDED) {
//                            // Tambahkan perangkat yang ditemukan jika belum ada
//                            val currentList = _scanResults.value.toMutableList()
//                            if (!currentList.contains(it)) {
//                                currentList.add(it)
//                                _scanResults.value = currentList.toList()
//                                Timber.d("Bluetooth Service: Found device: ${it.name}")
//                            }
//                        }
//                    }
//                }
//                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
//                    Timber.i("Bluetooth Service: Discovery started.")
//                    _scanResults.value = emptyList() // Kosongkan daftar saat mulai
//                }
//                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
//                    Timber.i("Bluetooth Service: Discovery finished.")
//                }
//            }
//        }
//    }
//
//    init {
//        // Daftarkan Receiver saat Service dibuat
//        val filter = IntentFilter().apply {
//            addAction(BluetoothDevice.ACTION_FOUND)
//            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
//            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
//        }
//        context.registerReceiver(discoveryReceiver, filter)
//
//        // Tambahkan perangkat yang sudah dipairing (terikat) secara default
//        bluetoothAdapter?.bondedDevices?.let { bondedDevices ->
//            _scanResults.value = bondedDevices.toList()
//        }
//    }
//
//    fun startDiscovery() {
//        if (bluetoothAdapter?.isDiscovering == true) {
//            bluetoothAdapter.cancelDiscovery()
//        }
//        bluetoothAdapter?.startDiscovery()
//    }
//
//    fun cancelDiscovery() {
//        bluetoothAdapter?.cancelDiscovery()
//    }
//
//    /**
//     * Mengirim data ESC/POS ke printer Bluetooth.
//     * @param deviceAddress Alamat MAC printer.
//     * @param data Byte array data ESC/POS.
//     * @return true jika berhasil terhubung dan mengirim data.
//     */
//    @SuppressLint("MissingPermission") // Izin harus ditangani di Fragment
//    suspend fun sendData(deviceAddress: String, data: ByteArray): Boolean = withContext(Dispatchers.IO) {
//        var socket: BluetoothSocket? = null
//
//        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
//            Timber.e("Bluetooth Service: Bluetooth tidak tersedia atau tidak aktif.")
//            return@withContext false
//        }
//
//        try {
//            val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress)
//
//            // 1. Buat socket koneksi SPP
//            socket = device.createRfcommSocketToServiceRecord(SPP_UUID)
//
//            // 2. Batalkan pemindaian yang mungkin aktif (koneksi lebih cepat)
//            bluetoothAdapter.cancelDiscovery()
//
//            // 3. Sambungkan socket (Blocking call)
//            socket.connect()
//            Timber.i("Bluetooth Service: Koneksi berhasil ke $deviceAddress.")
//
//            // 4. Kirim data
//            val outputStream = socket.outputStream
//            outputStream.write(data)
//            outputStream.flush()
//
//            // Berikan sedikit delay untuk memastikan printer memproses semua data
//            Thread.sleep(500)
//
//            return@withContext true
//
//        } catch (e: IOException) {
//            Timber.e(e, "Bluetooth Service: Gagal terhubung atau mengirim data ke $deviceAddress.")
//            // Tutup socket jika terbuka
//            try {
//                socket?.close()
//            } catch (closeException: IOException) {
//                Timber.e(closeException, "Bluetooth Service: Gagal menutup socket.")
//            }
//            return@withContext false
//        }
//    }
//}