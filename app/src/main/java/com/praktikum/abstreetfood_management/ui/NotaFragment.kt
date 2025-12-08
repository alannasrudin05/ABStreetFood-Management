package com.praktikum.abstreetfood_management.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.praktikum.abstreetfood_management.R
import com.praktikum.abstreetfood_management.data.adapter.ReceiptItemAdapter
import com.praktikum.abstreetfood_management.databinding.FragmentNotaBinding // <-- Perlu dibuat/diganti
import com.praktikum.abstreetfood_management.domain.model.Transaction
import com.praktikum.abstreetfood_management.domain.model.TransactionDetail
import com.praktikum.abstreetfood_management.domain.usecase.PrintUseCase
import com.praktikum.abstreetfood_management.viewmodel.TransaksiViewModel // Menggunakan ViewModel yang sama
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class NotaFragment : Fragment() {

    private var _binding: FragmentNotaBinding? = null
    private val binding get() = _binding!!

    // Mengambil argumen transactionId dari Navigation Component
    private val args: NotaFragmentArgs by navArgs()

    private val viewModel: TransaksiViewModel by viewModels()

    @Inject
    lateinit var printUseCase: PrintUseCase

    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply {
        minimumFractionDigits = 0
    }
    private val datetimeFormatter = SimpleDateFormat("dd MMM yyyy, HH:mm 'WIB'", Locale("in", "ID"))

    private val deviceMap = mutableMapOf<String, BluetoothDevice>()
    private var transactionDetailToPrint: TransactionDetail? = null
    private val TAG = "NOTA_LOG"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Asumsi: fragment_nota.xml adalah salinan dari dialog_receipt.xml
        _binding = FragmentNotaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val transactionId = args.transactionId
        loadReceiptData(transactionId)
    }

    private fun loadReceiptData(transactionId: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            val detail = viewModel.getTransactionDetailById(transactionId)
            if (detail != null) {
                transactionDetailToPrint = detail // Simpan untuk Print/Share
                bindReceiptData(detail)
                setupListeners()
            } else {
                Toast.makeText(requireContext(), "Gagal memuat detail nota.", Toast.LENGTH_LONG).show()
                // TODO: Handle navigasi kembali jika gagal
            }
        }
    }

    private fun bindReceiptData(detail: TransactionDetail) {
        val header = detail.header
        val transactionDate = Date(header.transactionTime)
        val cashierName = viewModel.currentUserName.value ?: header.userId // Ambil nama dari ViewModel

        binding.tvTransactionId.text = "Nota: #${header.id.take(8).uppercase(Locale.ROOT)}"
        binding.tvTransactionTime.text = "Tanggal: ${datetimeFormatter.format(transactionDate)}"
        binding.tvCashierName.text = "Kasir: $cashierName"
        binding.tvNote.text = "Catatan: ${header.note.ifEmpty { "Tidak ada catatan." }}"
        binding.tvSubtotal.text = currencyFormatter.format(header.subTotal)
        binding.tvGrandTotal.text = currencyFormatter.format(header.grandTotal)

        // Setup RecyclerView Item
        val receiptItemAdapter = ReceiptItemAdapter()
        binding.rvTransactionItems.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = receiptItemAdapter
        }
        receiptItemAdapter.submitList(detail.items)
    }

    private fun setupListeners() {
        // Listener Aksi (Tombol dari layout fragment_nota.xml)
        binding.btnShareReceipt.setOnClickListener {
            // Kita kirimkan root layout scrollview-nya untuk di-capture
            shareReceipt(binding.root.getChildAt(0))
        }

        binding.btnPrintReceipt.setOnClickListener {
            // Mulai cek izin & alur Bluetooth
            transactionDetailToPrint?.let {
                checkBluetoothPermissionsAndStartScan()
            }
        }

        // Tombol kembali (navigasi ke Home/Dashboard)
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    // =====================================================================================
    // 🔌 FUNGSI BLUETOOTH & SCANNING (Sama seperti yang Anda buat, tapi dipindahkan ke sini)
    // =====================================================================================

    private fun checkBluetoothPermissionsAndStartScan() {
        // ... (Implementasi Izin Bluetooth) ...
        // [Kode sama seperti di TransaksiFragment.kt]
        val permissions = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        }
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)

        val requiredPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
        }

        if (requiredPermissions.isNotEmpty()) {
            requestBluetoothPermissions.launch(requiredPermissions.toTypedArray())
        } else {
            checkBluetoothStatusAndScan()
        }
    }

    private val requestBluetoothPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value == true }) {
            checkBluetoothStatusAndScan()
        } else {
            Toast.makeText(requireContext(), "Izin Bluetooth atau Lokasi ditolak. Cetak gagal.", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkBluetoothStatusAndScan() {
        // ... (Implementasi Cek Status Bluetooth) ...
        // [Kode sama seperti di TransaksiFragment.kt]
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Toast.makeText(requireContext(), "Perangkat tidak mendukung Bluetooth.", Toast.LENGTH_LONG).show()
        } else if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBluetoothLauncher.launch(enableBtIntent)
        } else {
            startDeviceScan()
        }
    }
    private val enableBluetoothLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            startDeviceScan()
        } else {
            Toast.makeText(requireContext(), "Bluetooth harus diaktifkan untuk mencetak.", Toast.LENGTH_LONG).show()
        }
    }


    private fun startDeviceScan() {
        Toast.makeText(requireContext(), "Memindai perangkat Bluetooth terdekat...", Toast.LENGTH_SHORT).show()

        printUseCase.startScan()
        showPrinterSelectionDialog()
    }

    @SuppressLint("MissingPermission")
    private fun showPrinterSelectionDialog() {
        // ... (Implementasi Dialog Pemilihan Printer) ...
        // [Kode sama seperti di TransaksiFragment.kt]
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

        printUseCase.scanResults.observe(viewLifecycleOwner) { devices ->
            deviceNames.clear()
            deviceMap.clear()
            devices.forEach { device ->
                val name = device.name ?: "Unnamed Device (${device.address})"
                deviceNames.add(name)
                deviceMap[name] = device
            }
            adapter.notifyDataSetChanged()
        }

        selectionDialog.listView.setOnItemClickListener { _, _, position, _ ->
            printUseCase.stopScan()
            selectionDialog.dismiss()

            val deviceName = adapter.getItem(position)
            val selectedDevice = deviceMap[deviceName]
            val detail = transactionDetailToPrint

            if (selectedDevice != null && detail != null) {
                printViaBluetooth(selectedDevice.address, detail)
            } else {
                Toast.makeText(requireContext(), "Data transaksi hilang atau perangkat tidak valid.", Toast.LENGTH_SHORT).show()
            }
        }

        selectionDialog.show()
    }

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
    // FUNGSI SHARE
    // =====================================================================================

    private fun shareReceipt(viewRoot: View) {
        val transactionId = transactionDetailToPrint?.header?.id ?: "unknown"
//        val shareButton = viewRoot.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_share_receipt)
//        val printButton = viewRoot.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_print_receipt)

        val headerView = binding.headerNota // ✅ Ini adalah Toolbar/Header Anda
        val buttonContainer = binding.llActionButtons // ✅ Ini adalah Kontainer Tombol Aksi

        val initialHeaderVisibility = headerView.visibility
        val initialButtonsVisibility = buttonContainer.visibility
        // Sembunyikan tombol sebelum capture
//        val initialShareVisibility = shareButton.visibility
//        val initialPrintVisibility = printButton.visibility
//        shareButton.visibility = View.GONE
//        printButton.visibility = View.GONE

        headerView.visibility = View.GONE
        buttonContainer.visibility = View.GONE

        // Capture Bitmap
        val bitmap = createBitmapFromView(viewRoot)
        val file = saveBitmapToCache(bitmap, transactionId)

        // Tampilkan kembali tombol
//        shareButton.visibility = initialShareVisibility
//        printButton.visibility = initialPrintVisibility

        headerView.visibility = initialHeaderVisibility
        buttonContainer.visibility = initialButtonsVisibility

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

    // ... (Fungsi createBitmapFromView dan saveBitmapToCache tetap sama) ...
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}