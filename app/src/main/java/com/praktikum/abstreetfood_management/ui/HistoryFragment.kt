package com.praktikum.abstreetfood_management.ui

import android.Manifest // Wajib
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.praktikum.abstreetfood_management.data.adapter.HistoryAdapter
import com.praktikum.abstreetfood_management.data.adapter.ReceiptItemAdapter
import com.praktikum.abstreetfood_management.databinding.DialogReceiptBinding
import com.praktikum.abstreetfood_management.databinding.FragmentHistoryBinding
import com.praktikum.abstreetfood_management.domain.model.Transaction
import com.praktikum.abstreetfood_management.domain.model.TransactionDetail
import com.praktikum.abstreetfood_management.domain.usecase.PrintUseCase
import com.praktikum.abstreetfood_management.utility.PrintDocumentAdapterHelper
import com.praktikum.abstreetfood_management.viewmodel.TransaksiViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var historyAdapter: HistoryAdapter

    private val transaksiViewModel: TransaksiViewModel by viewModels()
//    private val printUseCase: PrintUseCase by viewModels() // UseCase untuk Scan/Print Bluetooth

    @Inject
    lateinit var printUseCase: PrintUseCase
    // --- Formatters ---
    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply {
        minimumFractionDigits = 0
    }
    private val datetimeFormatter = SimpleDateFormat("dd MMM yyyy, HH:mm 'WIB'", Locale("in", "ID"))
    private val TAG = "HISTORY_FRAGMENT"

    private var transactionDetailToPrint: TransactionDetail? = null // Data yang akan dicetak
    private val deviceMap = mutableMapOf<String, BluetoothDevice>() // Map untuk menyimpan hasil scan

    // =====================================================================================
    // 🔌 KONTRAK UNTUK IZIN BLUETOOTH & AKTIVASI (Wajib di level Fragment/Activity)
    // =====================================================================================

    private val requestBluetoothPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value == true }) {
            checkBluetoothStatusAndScan() // Lanjut setelah izin diberikan
        } else {
            Toast.makeText(requireContext(), "Izin Bluetooth atau Lokasi ditolak. Cetak gagal.", Toast.LENGTH_LONG).show()
        }
    }

    private val enableBluetoothLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            startDeviceScan() // Lanjutkan scan setelah Bluetooth aktif
        } else {
            Toast.makeText(requireContext(), "Bluetooth harus diaktifkan untuk mencetak.", Toast.LENGTH_LONG).show()
        }
    }

    // =====================================================================================
    // LIFECYCLE & SETUP
    // =====================================================================================

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadHistoryData()
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter { transaction ->
            loadAndShowReceipt(transaction.id)
        }

        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = historyAdapter
        }
    }

    private fun loadHistoryData() {
        transaksiViewModel.transactionHistory.observe(viewLifecycleOwner) { transactions ->
            if (transactions.isNotEmpty()) {
                historyAdapter.submitList(transactions)
            } else {
                historyAdapter.submitList(emptyList())
            }
        }
    }

    // =====================================================================================
    // LOGIKA DETAIL TRANSAKSI & DIALOG
    // =====================================================================================

    private fun loadAndShowReceipt(transactionId: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            val detail = transaksiViewModel.getTransactionDetailById(transactionId)
            if (detail != null) {
                transactionDetailToPrint = detail
                showReceiptDialog(detail)
            } else {
                Toast.makeText(requireContext(), "Gagal memuat detail nota.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showReceiptDialog(detail: TransactionDetail) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding = DialogReceiptBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.setContentView(dialogBinding.root)

        bindReceiptData(dialogBinding, detail.header)
        val receiptItemAdapter = ReceiptItemAdapter()
        dialogBinding.rvTransactionItems.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = receiptItemAdapter
        }
        receiptItemAdapter.submitList(detail.items)

        // Listener Aksi
        dialogBinding.btnShareReceipt.setOnClickListener {
            shareReceipt(dialogBinding.root, detail.header.id)
            dialog.dismiss()
        }

        // ✅ PICU ALUR CETAK BLUETOOTH
        dialogBinding.btnPrintReceipt.setOnClickListener {
            // Tutup dialog nota, lalu mulai cek izin
            dialog.dismiss()
            checkBluetoothPermissionsAndStartScan()
        }

        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun bindReceiptData(binding: DialogReceiptBinding, transaction: Transaction) {
        val transactionDate = Date(transaction.transactionTime)
        val cashierName = transaksiViewModel.currentUserName.value ?: transaction.userId
//        binding.tvTransactionId.text = "Nota: ${transaction.id.take(12)}"
        binding.tvTransactionId.text = "Nota: #${transaction.id.take(8).uppercase(Locale.ROOT)}"
        binding.tvTransactionTime.text = "Tanggal: ${datetimeFormatter.format(transactionDate)}"
        binding.tvCashierName.text = cashierName
        binding.tvNote.text = "Catatan: ${transaction.note.ifEmpty { "Tidak ada catatan." }}"
        binding.tvSubtotal.text = currencyFormatter.format(transaction.subTotal)
        binding.tvGrandTotal.text = currencyFormatter.format(transaction.grandTotal)
        // TODO: Lookup dan isi tv_cashier_name dan tv_outlet_location
    }

    // =====================================================================================
    // 🔌 FUNGSI BLUETOOTH & SCANNING
    // =====================================================================================

    /**
     * 1. Cek Izin -> Lanjutkan ke Cek Status Bluetooth
     */
    private fun checkBluetoothPermissionsAndStartScan() {
        val permissions = mutableListOf<String>()

        // Izin BLUETOOTH_CONNECT dan BLUETOOTH_SCAN (API 31+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        }
        // Izin Lokasi (Wajib untuk scan di API < 31)
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)

        val requiredPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
        }

        if (requiredPermissions.isNotEmpty()) {
            requestBluetoothPermissions.launch(requiredPermissions.toTypedArray())
        } else {
            checkBluetoothStatusAndScan() // Lanjut ke langkah 2
        }
    }

    /**
     * 2. Cek Status Bluetooth -> Aktifkan jika non-aktif
     */
    private fun checkBluetoothStatusAndScan() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Toast.makeText(requireContext(), "Perangkat tidak mendukung Bluetooth.", Toast.LENGTH_LONG).show()
        } else if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBluetoothLauncher.launch(enableBtIntent)
        } else {
            startDeviceScan() // Lanjut ke langkah 3
        }
    }

    /**
     * 3. Memulai pemindaian perangkat dan menampilkan dialog
     */
    private fun startDeviceScan() {
        Toast.makeText(requireContext(), "Memindai perangkat Bluetooth terdekat...", Toast.LENGTH_SHORT).show()

        // Memicu scan di Use Case
        printUseCase.startScan()

        // Menampilkan Dialog Pemilihan Printer (Langkah 4)
        showPrinterSelectionDialog()
    }

    /**
     * 4. Dialog untuk menampilkan list printer terdekat, dan memicu cetak saat diklik.
     */
    @SuppressLint("MissingPermission")
    private fun showPrinterSelectionDialog() {
        // ... (Logika List Adapter dan Dialog Builder) ...
        val deviceList = mutableListOf<BluetoothDevice>()
        val deviceNames = mutableListOf<String>()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, deviceNames)

        val selectionDialog = AlertDialog.Builder(requireContext())
            .setTitle("Pilih Printer Thermal")
            .setAdapter(adapter, null)
            .setNegativeButton("Batal") { d, _ ->
                printUseCase.stopScan()
                d.dismiss()
            }
            .create()

        // Amati hasil scan dari Use Case
        printUseCase.scanResults.observe(viewLifecycleOwner) { devices ->
            deviceList.clear()
            deviceNames.clear()
            deviceMap.clear() // Clear map saat ada hasil scan baru
            deviceList.addAll(devices)

            devices.forEach { device ->
                val name = device.name ?: "Unnamed Device (${device.address})"
                deviceNames.add(name)
                deviceMap[name] = device // Isi deviceMap
            }
            adapter.notifyDataSetChanged()
        }

        // Listener saat printer dipilih
        selectionDialog.listView.setOnItemClickListener { _, _, position, _ ->
            printUseCase.stopScan()
            selectionDialog.dismiss()

            val deviceName = adapter.getItem(position)
            val selectedDevice = deviceMap[deviceName] // Ambil dari map
            val detail = transactionDetailToPrint

            if (selectedDevice != null && detail != null) {
                printViaBluetooth(selectedDevice.address, detail)
            } else {
                Toast.makeText(requireContext(), "Data transaksi hilang atau perangkat tidak valid.", Toast.LENGTH_SHORT).show()
            }
        }

        selectionDialog.show()
    }

    /**
     * ✅ Fungsi Final: Memicu Use Case cetak Bluetooth.
     */
    private fun printViaBluetooth(deviceAddress: String, detail: TransactionDetail) {
        viewLifecycleOwner.lifecycleScope.launch {
            Toast.makeText(requireContext(), "Mencoba menyambung ke printer...", Toast.LENGTH_SHORT).show()

            val result = printUseCase.printViaBluetooth(deviceAddress, detail)

            if (result) {
                Toast.makeText(requireContext(), "Cetak Selesai!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), "Cetak Gagal. Cek koneksi & printer.", Toast.LENGTH_LONG).show()
            }
        }
    }

    // =====================================================================================
    // FUNGSI SHARE & PRINT MANAGER STANDAR
    // =====================================================================================

    private fun shareReceipt(viewRoot: View, transactionId: String) {
        val shareButton = viewRoot.findViewById<com.google.android.material.button.MaterialButton>(com.praktikum.abstreetfood_management.R.id.btn_share_receipt)
        val printButton = viewRoot.findViewById<com.google.android.material.button.MaterialButton>(com.praktikum.abstreetfood_management.R.id.btn_print_receipt)

        // Asumsi tombol-tombol berada di parent yang sama, atau Anda bisa sembunyikan container-nya.
        // Kita sembunyikan langsung:
        val initialShareVisibility = shareButton.visibility
        val initialPrintVisibility = printButton.visibility

        shareButton.visibility = View.GONE
        printButton.visibility = View.GONE

        val bitmap = createBitmapFromView(viewRoot)
        val file = saveBitmapToCache(bitmap, transactionId)
        if (file == null) {
            Toast.makeText(requireContext(), "Gagal menyiapkan file share.", Toast.LENGTH_SHORT).show()
            return
        }

        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            file
        )

        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/png"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, "Bagikan Nota via..."))
    }

    private fun createBitmapFromView(view: View): Bitmap {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun saveBitmapToCache(bitmap: Bitmap, transactionId: String): File? {
        val cachePath = File(requireContext().externalCacheDir, "receipt_images")
        cachePath.mkdirs()

        val file = File(cachePath, "nota_${transactionId.take(8)}.png")

        return try {
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
            stream.close()
            file
        } catch (e: Exception) {
            Log.e(TAG, "Gagal menyimpan bitmap ke cache", e)
            null
        }
    }

    private fun printReceipt(viewRoot: View, transactionId: String) {
        val jobName = "Nota_${transactionId.take(8)}"
        val printManager = requireContext().getSystemService(Context.PRINT_SERVICE) as PrintManager

        val printAdapter = PrintDocumentAdapterHelper.createPrintAdapter(viewRoot, requireContext(), jobName)

        printManager.print(
            jobName,
            printAdapter,
            PrintAttributes.Builder().build()
        )
        Toast.makeText(requireContext(), "Mempersiapkan cetak nota...", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // PENTING: Hentikan pemindaian jika fragment dihancurkan
        printUseCase.stopScan()
        _binding = null
    }
}


//package com.praktikum.abstreetfood_management.ui
//
//import android.Manifest
//import android.app.Dialog
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.graphics.Bitmap
//import android.graphics.Canvas
//import android.os.Bundle
//import android.print.PrintAttributes
//import android.print.PrintManager
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.view.Window
//import android.widget.Toast
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.ContextCompat
//import androidx.core.content.FileProvider
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.lifecycle.lifecycleScope
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.praktikum.abstreetfood_management.data.adapter.HistoryAdapter // Adapter baru
//import com.praktikum.abstreetfood_management.data.adapter.ReceiptItemAdapter
//import com.praktikum.abstreetfood_management.databinding.DialogReceiptBinding
//import com.praktikum.abstreetfood_management.databinding.FragmentHistoryBinding
//import com.praktikum.abstreetfood_management.domain.model.Transaction // Model yang akan dipakai
//import com.praktikum.abstreetfood_management.domain.model.TransactionDetail
//import com.praktikum.abstreetfood_management.domain.usecase.PrintUseCase
//import com.praktikum.abstreetfood_management.utility.PrintDocumentAdapterHelper
//import com.praktikum.abstreetfood_management.viewmodel.TransaksiViewModel
//import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.coroutines.launch
//import java.io.File
//import java.io.FileOutputStream
//import java.text.NumberFormat
//import java.text.SimpleDateFormat
//import java.util.Date
//import java.util.Locale
//import kotlin.getValue
//
//@AndroidEntryPoint
//class HistoryFragment : Fragment() {
//
//    private var _binding: FragmentHistoryBinding? = null
//    private val binding get() = _binding!!
//
//    private lateinit var historyAdapter: HistoryAdapter
//
//    private val transaksiViewModel: TransaksiViewModel by viewModels()
//
//    // --- Formatters ---
//    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply {
//        minimumFractionDigits = 0
//    }
//    private val datetimeFormatter = SimpleDateFormat("dd MMM yyyy, HH:mm 'WIB'", Locale("in", "ID"))
//
//    private val TAG = "HISTORY_FRAGMENT"
//
//    private val printUseCase: PrintUseCase by viewModels()
//    private var transactionDetailToPrint: TransactionDetail? = null
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        setupRecyclerView()
//        loadHistoryData()
//    }
//
//    private fun setupRecyclerView() {
//        // TODO: Anda perlu membuat HistoryAdapter dan HistoryDiffCallback
//         historyAdapter = HistoryAdapter { transaction ->
//             loadAndShowReceipt(transaction.id)
//         }
//
//         binding.rvHistory.apply {
//             layoutManager = LinearLayoutManager(context)
//             adapter = historyAdapter
//         }
//    }
//
//    private fun loadHistoryData() {
//        // Mengamati LiveData riwayat transaksi dari ViewModel
//        transaksiViewModel.transactionHistory.observe(viewLifecycleOwner) { transactions ->
//            if (transactions.isNotEmpty()) {
//                // Tampilkan data ke RecyclerView
//                historyAdapter.submitList(transactions)
//                // TODO: Sembunyikan state kosong/loading
//            } else {
//                // TODO: Tampilkan state kosong (e.g., TextView "Belum ada transaksi")
//                historyAdapter.submitList(emptyList())
//            }
//        }
//    }
//
//    /**
//     * Memuat data detail transaksi (header + item) dari Repository dan menampilkan dialog.
//     */
//    private fun loadAndShowReceipt(transactionId: String) {
//        // TODO: Anda perlu menambahkan LiveData/fungsi di TransaksiViewModel
//        // untuk memuat getTransactionDetail(transactionId).
//        // Untuk saat ini, kita akan simulasi data atau memanggil langsung dari ViewModel
//
//        // Contoh: Memanggil fungsi yang harus Anda buat di ViewModel
//        // transaksiViewModel.loadTransactionDetail(transactionId).observe(viewLifecycleOwner) { result ->
//        //     when (result) {
//        //         is Result.Success -> showReceiptDialog(result.data)
//        //         is Result.Error -> Toast.makeText(requireContext(), "Gagal memuat detail", Toast.LENGTH_SHORT).show()
//        //         ...
//        //     }
//        // }
//
//        // KARENA VIEWMODEL BELUM MEMILIKI loadTransactionDetail, kita akan menggunakan data placeholder.
////        val placeholderTransaction = Transaction(
////            id = transactionId, userId = "cashier-123", outletId = "outlet-1",
////            subTotal = 14000.0, grandTotal = 14000.0, note = "Pesanan cepat.",
////            transactionTime = System.currentTimeMillis()
////        )
////        // Jika detail tidak diambil, kita hanya menampilkan data header yang sudah ada.
////        showReceiptDialog(placeholderTransaction)
//
//        viewLifecycleOwner.lifecycleScope.launch {
//            // Panggil fungsi Repository/UseCase dari ViewModel/Repository
////            val detail = transaksiViewModel.getTransactionDetailById(transactionId)
//
//            val detail = transaksiViewModel.getTransactionDetailById(transactionId) // ✅ Panggilan langsung
//            if (detail != null) {
//                showReceiptDialog(detail)
//            } else {
//                Toast.makeText(requireContext(), "Gagal memuat detail nota.", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//
//    /**
//     * 🧾 Menampilkan Dialog Struk (Nota)
//     */
//
//    private fun showReceiptDialog(detail: TransactionDetail) { // ✅ Menerima TransactionDetail
//        val dialog = Dialog(requireContext())
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        val dialogBinding = DialogReceiptBinding.inflate(LayoutInflater.from(requireContext()))
//        dialog.setContentView(dialogBinding.root)
//
//        // 1. Mengisi Data Header
//        bindReceiptData(dialogBinding, detail.header)
//
//        // 2. Mengisi Data Item List
//        val receiptItemAdapter = ReceiptItemAdapter()
//        dialogBinding.rvTransactionItems.apply {
//            layoutManager = LinearLayoutManager(context)
//            adapter = receiptItemAdapter
//        }
//        receiptItemAdapter.submitList(detail.items) // ✅ Mengisi RecyclerView item
//
//        // 2. Menghubungkan Listener Tombol Aksi (BARU)
//        dialogBinding.btnShareReceipt.setOnClickListener {
//            shareReceipt(dialogBinding.root, detail.header.id)
//            dialog.dismiss() // Tutup dialog setelah aksi
//        }
//        dialogBinding.btnPrintReceipt.setOnClickListener {
//            printReceipt(dialogBinding.root, detail.header.id)
////            if (checkBluetoothPermissions()) {
////                printViaBluetooth(PRINTER_MAC_ADDRESS, detail)
////            } else {
////                Toast.makeText(requireContext(), "Izinkan akses Bluetooth dan Lokasi.", Toast.LENGTH_LONG).show()
////            }
//            dialog.dismiss() // Tutup dialog setelah aksi
//        }
//
//        dialog.show()
//        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//    }
////    private fun showReceiptDialog(transaction: Transaction) {
////        // Menggunakan Dialog kustom untuk tampilan mirip struk
////        val dialog = Dialog(requireContext())
////        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
////
////        // Menggunakan view binding untuk layout dialog_receipt.xml
////        val dialogBinding = DialogReceiptBinding.inflate(LayoutInflater.from(requireContext()))
////        dialog.setContentView(dialogBinding.root)
////
////        // Mengisi data header transaksi
////        bindReceiptData(dialogBinding, transaction)
////
//////        val receiptItemAdapter = ReceiptItemAdapter()
//////        dialogBinding.rvTransactionItems.apply {
//////            layoutManager = LinearLayoutManager(context)
//////            adapter = receiptItemAdapter
//////        }
//////        receiptItemAdapter.submitList(detail.items)
////
////        dialog.show()
////        // Opsional: Atur lebar dialog agar penuh
////        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
////    }
////
////    /**
////     * Mengikat data transaksi (header) ke View IDs di dialog_receipt.xml.
////     */
//    private fun bindReceiptData(binding: DialogReceiptBinding, transaction: Transaction) {
//
//        // Konversi Timestamp
//        val transactionDate = Date(transaction.transactionTime)
//
//        // 1. Detail Transaksi
//        binding.tvTransactionId.text = "Nota: ${transaction.id}"
//        binding.tvTransactionTime.text = "Tanggal: ${datetimeFormatter.format(transactionDate)}"
//        // binding.tvCashierName.text = "Kasir: ${transaction.userId} (perlu lookup nama)" // Opsional
//        binding.tvNote.text = "Catatan: ${transaction.note.ifEmpty { "Tidak ada catatan." }}"
//
//        // 2. Total
//        binding.tvSubtotal.text = currencyFormatter.format(transaction.subTotal)
//        binding.tvGrandTotal.text = currencyFormatter.format(transaction.grandTotal)
//
//        // TODO: Isi RecyclerView item (rv_transaction_items)
//    }
//
//    /**
//     * Mengambil View (Nota), mengkonversinya menjadi Bitmap, dan membagikannya.
//     * @param viewRoot View root dari layout dialog_receipt.xml
//     * @param transactionId ID Transaksi untuk nama file
//     */
//    private fun shareReceipt(viewRoot: View, transactionId: String) {
//        // 1. Konversi View menjadi Bitmap
//        val bitmap = createBitmapFromView(viewRoot)
//
//        // 2. Simpan Bitmap ke cache untuk dibagikan
//        val file = saveBitmapToCache(bitmap, transactionId)
//        if (file == null) {
//            Toast.makeText(requireContext(), "Gagal menyiapkan file share.", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        // 3. Buat URI yang aman menggunakan FileProvider
//        val uri = FileProvider.getUriForFile(
//            requireContext(),
//            "${requireContext().packageName}.provider", // Asumsi provider name ini ada di Manifest
//            file
//        )
//
//        // 4. Buat Intent Share
//        val shareIntent: Intent = Intent().apply {
//            action = Intent.ACTION_SEND
//            putExtra(Intent.EXTRA_STREAM, uri)
//            type = "image/png"
//            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//        }
//        startActivity(Intent.createChooser(shareIntent, "Bagikan Nota via..."))
//    }
//
//
//    /**
//     * Mengubah View menjadi Bitmap.
//     */
//    private fun createBitmapFromView(view: View): Bitmap {
//        // Pastikan view memiliki ukuran yang terukur
//        view.measure(
//            View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
//            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
//        )
//        // Atur layout setelah pengukuran untuk mendapatkan ukuran yang benar
//        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
//
//        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
//        val canvas = Canvas(bitmap)
//        view.draw(canvas)
//        return bitmap
//    }
//
//    /**
//     * Menyimpan Bitmap ke Cache (diperlukan untuk FileProvider).
//     */
//    private fun saveBitmapToCache(bitmap: Bitmap, transactionId: String): File? {
//        val cachePath = File(requireContext().externalCacheDir, "receipt_images")
//        cachePath.mkdirs()
//
//        val file = File(cachePath, "nota_${transactionId.take(8)}.png")
//
//        return try {
//            val stream = FileOutputStream(file)
//            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
//            stream.close()
//            file
//        } catch (e: Exception) {
////            Log.e(e, "Gagal menyimpan bitmap ke cache")
//            Log.e(TAG, "Gagal menyimpan bitmap ke cache", e)
//            null
//        }
//    }
//
//    /**
//     * Memicu layanan cetak Android untuk mencetak nota (sebagai dokumen web/gambar).
//     * @param viewRoot View root dari layout dialog_receipt.xml
//     * @param transactionId ID Transaksi
//     */
//    private fun printReceipt(viewRoot: View, transactionId: String) {
//        val jobName = "Nota_${transactionId.take(8)}"
//        val printManager = requireContext().getSystemService(Context.PRINT_SERVICE) as PrintManager
//
//        val printAdapter = PrintDocumentAdapterHelper.createPrintAdapter(viewRoot, requireContext(), jobName)
//
//
//        printManager.print(
//            jobName,
//            printAdapter,
//            PrintAttributes.Builder().build()
//        )
//        Toast.makeText(requireContext(), "Mempersiapkan cetak nota...", Toast.LENGTH_SHORT).show()
//    }
//
//    /**
//     * Fungsi yang memanggil Use Case untuk mencetak.
//     */
//    private fun printViaBluetooth(deviceAddress: String, detail: TransactionDetail) {
//        viewLifecycleOwner.lifecycleScope.launch {
//            Toast.makeText(requireContext(), "Mencoba menyambung ke printer...", Toast.LENGTH_SHORT).show()
//
//            val result = printUseCase.printViaBluetooth(deviceAddress, detail)
//
//            if (result) {
//                Toast.makeText(requireContext(), "Cetak Selesai!", Toast.LENGTH_LONG).show()
//            } else {
//                Toast.makeText(requireContext(), "Cetak Gagal. Cek Bluetooth dan MAC.", Toast.LENGTH_LONG).show()
//            }
//        }
//    }
//
//    // ⚠️ FUNGSI PENTING: Cek Izin (Hanya untuk referensi, implementasi penuh lebih kompleks)
//    private fun checkBluetoothPermissions(): Boolean {
//        // Anda harus mengimplementasikan pengecekan izin BLUETOOTH_CONNECT, BLUETOOTH_SCAN,
//        // dan ACCESS_FINE_LOCATION di Android 12+
//        return ContextCompat.checkSelfPermission(
//            requireContext(),
//            Manifest.permission.BLUETOOTH_CONNECT
//        ) == PackageManager.PERMISSION_GRANTED
//        // ... (Logika penanganan permintaan izin yang sebenarnya di sini)
//    }
//
//    // Kontrak untuk meminta izin multi-permission (Wajib untuk scan/connect)
//    private val requestBluetoothPermissions = registerForActivityResult(
//        ActivityResultContracts.RequestMultiplePermissions()
//    ) { permissions ->
//        if (permissions.all { it.value == true }) { // Cek semua izin diberikan
//            checkBluetoothStatusAndScan()
//        } else {
//            Toast.makeText(requireContext(), "Izin Bluetooth atau Lokasi ditolak. Cetak gagal.", Toast.LENGTH_LONG).show()
//        }
//    }
//
//    // Kontrak untuk mengaktifkan Bluetooth jika tidak aktif
//    private val enableBluetoothLauncher = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        if (result.resultCode == AppCompatActivity.RESULT_OK) {
//            startDeviceScan() // Lanjutkan scan setelah Bluetooth aktif
//        } else {
//            Toast.makeText(requireContext(), "Bluetooth harus diaktifkan untuk mencetak.", Toast.LENGTH_LONG).show()
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}