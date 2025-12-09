//package com.praktikum.abstreetfood_management.ui
//
//import android.annotation.SuppressLint
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.RadioButton
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.praktikum.abstreetfood_management.databinding.FragmentTransaksiBinding
//import com.praktikum.abstreetfood_management.domain.model.NewTransactionItem
//import com.praktikum.abstreetfood_management.domain.model.ProductItem
//import com.praktikum.abstreetfood_management.ui.dialog.VariantSelectionListener
//import com.praktikum.abstreetfood_management.data.adapter.CartAdapter // Asumsi Adapter baru
//import timber.log.Timber
//import java.text.NumberFormat
//import java.util.Locale
//
//// TransaksiFragment harus mengimplementasikan listener
//class TransaksiFragment : Fragment(), VariantSelectionListener {
//
//    private var _binding: FragmentTransaksiBinding? = null
//    private val binding get() = _binding!!
//
//    private lateinit var cartAdapter: CartAdapter
//
//    private var selectedProductItem: ProductItem? = null
//
//    // Data lokal yang menampung item keranjang saat ini
//    private val cartItems = mutableListOf<NewTransactionItem>()
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentTransaksiBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        setupHeader()
//        setupCartRecyclerView()
//        setupTopProductListeners()
//        updateTotalDisplay()
//    }
//
//    private fun setupHeader() {
//        binding.ivBack.setOnClickListener {
//            // TODO: Handle back navigation
//        }
//    }
//
//    private fun setupCartRecyclerView() {
//        // TODO: Anda perlu membuat CartAdapter dan CartDiffCallback
//        // cartAdapter = CartAdapter { item -> /* handle remove/change quantity */ }
//
//        // --- ASUMSI CartAdapter sudah dibuat ---
//        // cartAdapter = CartAdapter()
//
//        // binding.rvCartItems.apply { // Asumsi ID RV sudah diperbaiki
//        //     layoutManager = LinearLayoutManager(context)
//        //     adapter = cartAdapter
//        // }
//    }
//
//    @SuppressLint("TimberArgCount")
//    private fun setupTopProductListeners() {
//        // NOTE: Karena layout Top Products Anda hardcoded, kita harus set listener pada CardView secara individual.
//
//        // --- ASUMSI Anda memiliki model ProductItem untuk item Ayam Bakar/Goreng ---
//        val ayamGorengDefault = ProductItem("ag_id", "Ayam", "Ayam Goreng", 10000.0, 10000.0, "NASI_BIASA")
//        val ayamBakarDefault = ProductItem("ab_id", "Ayam", "Ayam Bakar", 10000.0, 10000.0, "NASI_BIASA")
//
//        // Klik Ayam Goreng
//         binding.llTopProducts.getChildAt(0).setOnClickListener {
//             Timber.d("TRANSAKSI_CLICK", "Produk Ayam Goreng diklik. Memanggil dialog.")
//             showVariantSelectionDialog(ayamGorengDefault)
//         }
//
//        // Klik Ayam Bakar
//         binding.llTopProducts.getChildAt(1).setOnClickListener {
//             Timber.d("TRANSAKSI_CLICK", "Produk Ayam Bakar diklik. Memanggil dialog.")
//             showVariantSelectionDialog(ayamBakarDefault)
//         }
//    }
//
//    private fun showVariantSelectionDialog(item: ProductItem) {
////        val dialog = ProductVariantDialog.newInstance(item)
//        selectedProductItem = item // Simpan data item yang diklik
//        // PENTING: Set target fragment untuk menerima hasil
////        dialog.setTargetFragment(this, 0)
////        dialog.show(parentFragmentManager, ProductVariantDialog.TAG)
//        // 1. Tampilkan View (Misalnya dengan animasi slide up)
//        binding.bottomSheetDialog.visibility = View.VISIBLE
//
//        // 2. Isi data ke elemen-elemen di dalam bottomSheetDialog
//        binding.tvVariantTitle.text = "Pilih Varian untuk ${item.name}"
//
//        // 3. Reset pilihan dan kalkulasi awal (default Nasi Biasa)
//        binding.rgVarian.clearCheck()
//        binding.rbNasiBiasa.isChecked = true
//        calculateAndDisplayPrice(item, "NASI_BIASA")
//
//        // 4. Set listener untuk kalkulasi real-time di sini
//        binding.rgVarian.setOnCheckedChangeListener { _, checkedId ->
//            val selectedTag = view?.findViewById<RadioButton>(checkedId)?.tag as? String ?: "TANPA_NASI"
//            calculateAndDisplayPrice(item, selectedTag)
//        }
//    }
//
////    private fun calculatePrice(variantKey: String): Double {
////        val modifier = variantModifiers[variantKey] ?: 0.0
////        return productItem.sellingPrice + modifier
////    }
//
//    private val variantModifiers = mapOf(
//        "TANPA_NASI" to 0.0,
//        "NASI_BIASA" to 3000.0,
//        "NASI_DOUBLE" to 5000.0
//    )
//    private lateinit var productItem: ProductItem
//
//    private fun calculateAndDisplayPrice(variantKey: String) {
//        val modifier = variantModifiers[variantKey] ?: 0.0
//
//        // Kalkulasi: Harga Jual (sellingPrice) + Modifier Varian
//        val newPrice = productItem.sellingPrice + modifier
//
//        // Format Rupiah
//        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
//
//        // Tampilkan harga yang dikalkulasikan
//        binding.tvCalculatedPrice.text = formatRupiah.format(newPrice)
//    }
//
//    private fun updateTotalDisplay() {
//        val total = cartItems.sumOf { it.itemPrice * it.quantity }
//        // TODO: Format total dan set ke binding.tvTotal
//    }
//
//    // --- IMPLEMENTASI VARIANT SELECTION LISTENER ---
//    override fun onVariantItemAdded(item: NewTransactionItem) {
//        // 1. Tambahkan item hasil kalkulasi dari dialog ke list lokal
//        cartItems.add(item)
//
//        // 2. Update RecyclerView (Asumsi CartAdapter sudah dibuat)
//        // cartAdapter.submitList(cartItems.toList())
//
//        // 3. Perbarui Total
//        updateTotalDisplay()
//
//        Toast.makeText(requireContext(), "Keranjang diperbarui.", Toast.LENGTH_SHORT).show()
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}

//
//package com.praktikum.abstreetfood_management.ui
//
//import android.annotation.SuppressLint
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.RadioButton
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.praktikum.abstreetfood_management.data.adapter.CartAction
//import com.praktikum.abstreetfood_management.data.adapter.CartActionListener
//import com.praktikum.abstreetfood_management.data.adapter.CartAdapter
//import com.praktikum.abstreetfood_management.databinding.FragmentTransaksiBinding
//import com.praktikum.abstreetfood_management.domain.model.NewTransactionItem
//import com.praktikum.abstreetfood_management.domain.model.ProductItem
//import com.praktikum.abstreetfood_management.ui.dialog.VariantSelectionListener
//import timber.log.Timber
//import java.text.NumberFormat
//import java.util.Locale
//
//// TransaksiFragment mengimplementasikan listener, meskipun menggunakan View tersembunyi,
//// kita akan mempertahankan interface ini jika nanti beralih kembali ke Dialog.
//class TransaksiFragment : Fragment(), VariantSelectionListener, CartActionListener {
//
//    private var _binding: FragmentTransaksiBinding? = null
//    private val binding get() = _binding!!
//
//    // Variabel untuk Adapter dan keranjang
//    private lateinit var cartAdapter: CartAdapter
//    private val cartItems = mutableListOf<NewTransactionItem>()
//
//    // Data item yang saat ini sedang dipilih di View Varian
//    private var selectedProductItem: ProductItem? = null
//
//    // Modifiers Varian (tetap di Fragment ini)
//    private val variantModifiers = mapOf(
//        "TANPA_NASI" to 0.0,
//        "NASI_BIASA" to 3000.0,
//        "NASI_DOUBLE" to 5000.0
//    )
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentTransaksiBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        binding.overlayView.setOnClickListener {
//            hideVariantSelectionView()
//        }
//
//        setupHeader()
//        setupCartRecyclerView()
//        setupTopProductListeners()
//        setupVariantViewListeners() // <<< BARU: Listener untuk View Varian
//        updateTotalDisplay()
//    }
//
//    private fun setupHeader() {
//        binding.ivBack.setOnClickListener {
//            // Jika bottomSheetDialog terlihat, tutup dulu
//            if (binding.bottomSheetDialog.visibility == View.VISIBLE) {
//                binding.bottomSheetDialog.visibility = View.GONE
//            } else {
//                // TODO: Handle back navigation (misalnya, findNavController().popBackStack())
//            }
//        }
//    }
//
//    private fun setupCartRecyclerView() {
//        cartAdapter = CartAdapter(this)
//
//        // Inisialisasi RecyclerView
//        binding.rvCartItems.apply {
//            layoutManager = LinearLayoutManager(context)
//            adapter = cartAdapter
//        }
//
//        // Memuat item yang sudah ada (jika ada)
//        cartAdapter.submitList(cartItems.toList())
//    }
//
//
//
//    private fun updateTotalDisplay() {
//        val total = cartItems.sumOf { it.itemPrice * it.quantity }
//        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
//        binding.tvTotal.text = formatRupiah.format(total)
//
//        // Update adapter (untuk menampilkan item baru)
//         cartAdapter.submitList(cartItems.toList())
//    }
//
//    // --- IMPLEMENTASI CART ACTION LISTENER (Dari Adapter) ---
//    override fun invoke(item: NewTransactionItem, action: CartAction) {
//        val index = cartItems.indexOfFirst { it.productItemId == item.productItemId } // Asumsi: Kita cari berdasarkan ID produk
//        if (index == -1) return
//
//        when (action) {
//            CartAction.ADD -> {
//                // Buat item baru dengan kuantitas +1
//                val updatedItem = item.copy(quantity = item.quantity + 1)
//                cartItems[index] = updatedItem
//            }
//            CartAction.REMOVE -> {
//                if (item.quantity > 1) {
//                    val updatedItem = item.copy(quantity = item.quantity - 1)
//                    cartItems[index] = updatedItem
//                } else {
//                    // Jika kuantitas 1, hapus item dari keranjang
//                    cartItems.removeAt(index)
//                }
//            }
//            CartAction.DELETE -> {
//                cartItems.removeAt(index)
//            }
//        }
//        // Selalu panggil updateTotalDisplay untuk trigger submitList dan update total
//        updateTotalDisplay()
//    }
//
//    @SuppressLint("TimberArgCount")
//    private fun setupTopProductListeners() {
//        // NOTE: Layout Anda memiliki dua CardView hardcoded di dalam llTopProducts.
//        // Asumsi ProductItem dibuat di sini (Seharusnya dari ViewModel/Repository)
//        val ayamGorengDummy = ProductItem("ag_id", "P001", "Ayam Goreng", 7000.0, 10000.0, "STANDAR")
//        val ayamBakarDummy = ProductItem("ab_id", "P002", "Ayam Bakar", 7500.0, 10000.0, "STANDAR")
//
//        // 1. Klik Ayam Goreng
//        (binding.llTopProducts.getChildAt(0) as? ViewGroup)?.getChildAt(0)?.setOnClickListener {
//            Timber.d("TRANSAKSI_CLICK", "Produk Ayam Goreng diklik. Memanggil view varian.")
//            showVariantSelectionView(ayamGorengDummy)
//        }
//        // 2. Klik Ayam Bakar
//        (binding.llTopProducts.getChildAt(1) as? ViewGroup)?.getChildAt(0)?.setOnClickListener {
//            Timber.d("TRANSAKSI_CLICK", "Produk Ayam Bakar diklik. Memanggil view varian.")
//            showVariantSelectionView(ayamBakarDummy)
//        }
//    }
//
//    private fun setupVariantViewListeners() {
//        // Listener untuk kalkulasi real-time saat RadioButton diubah
//        binding.rgVarian.setOnCheckedChangeListener { _, checkedId ->
//            val selectedTag = view?.findViewById<RadioButton>(checkedId)?.tag as? String ?: "TANPA_NASI"
//            // Panggil kalkulasi dengan item yang sedang aktif
//            selectedProductItem?.let { item ->
//                calculateAndDisplayPrice(item, selectedTag)
//            }
//        }
//
//        // Listener Tombol "Tambahkan ke Keranjang"
//        binding.btnAddToCart.setOnClickListener {
//            addToCartFromVariantView()
//        }
//    }
//
//    // Fungsi Utama untuk Menampilkan View Varian
//    @SuppressLint("TimberArgCount")
//    private fun showVariantSelectionView(item: ProductItem) {
//        selectedProductItem = item // Simpan data item yang sedang aktif
//
////        binding.bottomSheetDialog.visibility = View.VISIBLE
////        binding.tvVariantTitle.text = "Pilih Varian untuk ${item.name}"
////
////        // Reset dan atur default selection
////        binding.rgVarian.clearCheck()
////        binding.rbNasiBiasa.isChecked = true
////        calculateAndDisplayPrice(item, "NASI_BIASA")
//        // 1. Tampilkan Overlay dan Bottom Sheet View
//        binding.overlayView.visibility = View.VISIBLE
//        binding.bottomSheetDialog.visibility = View.VISIBLE
//
//        // 2. Animasikan Bottom Sheet (opsional, untuk tampilan profesional)
//        binding.bottomSheetDialog.animate()
//            .translationY(0f) // Pindahkan ke posisi y=0 (di dalam layar)
//            .setDuration(300)
//            .start()
//
//        // 3. Isi data dan kalkulasi
//        binding.tvVariantTitle.text = "Pilih Varian untuk ${item.name}"
//        binding.rgVarian.clearCheck()
//        binding.rbNasiBiasa.isChecked = true
//        calculateAndDisplayPrice(item, "NASI_BIASA")
//
//        Timber.i("VARIANT_VIEW", "View Varian ditampilkan untuk produk: ${item.name}")
//    }
//
//    private fun hideVariantSelectionView() {
//        // 1. Animasikan Bottom Sheet keluar layar
//        binding.bottomSheetDialog.animate()
//            .translationY(binding.bottomSheetDialog.height.toFloat())
//            .setDuration(300)
//            .withEndAction {
//                // 2. Setelah animasi selesai, sembunyikan View dan Overlay
//                binding.bottomSheetDialog.visibility = View.GONE
//                binding.overlayView.visibility = View.GONE
//                selectedProductItem = null
//            }
//            .start()
//    }
//
//    // Logika Simpan ke Keranjang
//    private fun addToCartFromVariantView() {
//        val currentItem = selectedProductItem ?: return // Harus ada item terpilih
//
//        val selectedRadioButtonId = binding.rgVarian.checkedRadioButtonId
//        val selectedRadioButton = view?.findViewById<RadioButton>(selectedRadioButtonId)
//
//        if (selectedRadioButton == null) {
//            Toast.makeText(requireContext(), "Pilih varian terlebih dahulu.", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val variantTag = selectedRadioButton.tag as String
//        val finalPrice = calculatePrice(currentItem, variantTag)
//
//        // 1. Buat NewTransactionItem
//        val newItem = NewTransactionItem(
//            productItemId = currentItem.id,
//            quantity = 1,
//            itemPrice = finalPrice,
//            isExtraRice = variantTag.contains("NASI")
//        )
//
//        // 2. Kirim ke fungsi penambahan keranjang
//        onVariantItemAdded(newItem)
//
//        // 3. Sembunyikan View
////        binding.bottomSheetDialog.visibility = View.GONE
//        hideVariantSelectionView()
//        selectedProductItem = null // Reset item yang dipilih
//
//    }
//
//    private fun calculatePrice(item: ProductItem, variantKey: String): Double {
//        val modifier = variantModifiers[variantKey] ?: 0.0
//        // Gunakan sellingPrice dari ProductItem yang diklik
//        return item.sellingPrice + modifier
//    }
//
//    private fun calculateAndDisplayPrice(item: ProductItem, variantKey: String) {
//        val newPrice = calculatePrice(item, variantKey)
//        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
//
//        // Tampilkan harga yang dikalkulasikan
//        binding.tvCalculatedPrice.text = formatRupiah.format(newPrice)
//    }
//
//
//
//    // --- IMPLEMENTASI VARIANT SELECTION LISTENER ---
//    override fun onVariantItemAdded(item: NewTransactionItem) {
//        // Logika ini dipanggil dari addToCartFromVariantView()
//        cartItems.add(item)
//        updateTotalDisplay()
//
//        Toast.makeText(requireContext(), "${item.productItemId} ditambahkan ke keranjang.", Toast.LENGTH_SHORT).show()
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}

package com.praktikum.abstreetfood_management.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import com.praktikum.abstreetfood_management.R
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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.praktikum.abstreetfood_management.data.adapter.CartAction
import com.praktikum.abstreetfood_management.data.adapter.CartActionListener
import com.praktikum.abstreetfood_management.data.adapter.CartAdapter
import com.praktikum.abstreetfood_management.data.adapter.ReceiptItemAdapter
import com.praktikum.abstreetfood_management.databinding.DialogChangeBinding
import com.praktikum.abstreetfood_management.databinding.DialogReceiptBinding
import com.praktikum.abstreetfood_management.databinding.FragmentTransaksiBinding
import com.praktikum.abstreetfood_management.domain.model.NewTransaction
import com.praktikum.abstreetfood_management.domain.model.NewTransactionItem
import com.praktikum.abstreetfood_management.domain.model.ProductItem
import com.praktikum.abstreetfood_management.domain.model.Transaction
import com.praktikum.abstreetfood_management.domain.model.TransactionDetail
import com.praktikum.abstreetfood_management.domain.usecase.PrintUseCase
import com.praktikum.abstreetfood_management.ui.dialog.VariantSelectionListener
import com.praktikum.abstreetfood_management.viewmodel.AuthViewModel
import com.praktikum.abstreetfood_management.viewmodel.TransaksiViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject
import kotlin.collections.get
import kotlin.collections.set
import kotlin.text.ifEmpty

// TransaksiFragment mengimplementasikan VariantSelectionListener (dari View Varian)
// dan CartActionListener (dari tombol + / - di keranjang)
@AndroidEntryPoint
class TransaksiFragment : Fragment(), VariantSelectionListener, CartActionListener {

    private var _binding: FragmentTransaksiBinding? = null
    private val binding get() = _binding!!

    // Variabel untuk Adapter dan keranjang
    private lateinit var cartAdapter: CartAdapter
    private val cartItems = mutableListOf<NewTransactionItem>()

    private val viewModel: TransaksiViewModel by viewModels() // <-- BARU

    private val authViewModel: AuthViewModel by viewModels()

    private var currentLoggedInUserId: String? = null // <-- Variabel lokal untuk menyimpan ID
    private var loadedProductItems: List<ProductItem> = emptyList()

    // Data item yang saat ini sedang dipilih di View Varian
    private var selectedProductItem: ProductItem? = null

    @Inject
    lateinit var printUseCase: PrintUseCase

//    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply {
//        minimumFractionDigits = 0
//    }
//    private val datetimeFormatter = SimpleDateFormat("dd MMM yyyy, HH:mm 'WIB'", Locale("in", "ID"))
//    private val deviceMap = mutableMapOf<String, BluetoothDevice>() // Map untuk menyimpan hasil scan
//    private var transactionDetailToPrint: TransactionDetail? = null

    private var availableVariantsMap: Map<String, ProductItem> = emptyMap()

    private val TAG = "TRANSAKSI_LOG"
    // Modifiers Varian (tetap di Fragment ini)
//    private val variantModifiers = mapOf(
//        "TANPA_NASI" to 0.0,
//        "NASI_BIASA" to 3000.0,
//        "NASI_DOUBLE" to 6000.0
//    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransaksiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.overlayView.setOnClickListener {
            hideVariantSelectionView()
        }

        setupHeader()
        setupCartRecyclerView()
        setupTopProductListeners()
        setupVariantViewListeners()


        setupSaveButton()
        updateTotalDisplay()
        setupObservers()
    }

    // [BARU] Setup Tombol Simpan
    private fun setupSaveButton() {
        binding.btnSimpan.setOnClickListener {
            // Cek apakah keranjang kosong
            if (cartItems.isEmpty()) {
                Toast.makeText(requireContext(), "Keranjang tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO: Tampilkan Dialog konfirmasi pembayaran/uang tunai di sini sebelum memanggil processAndSaveTransaction
            // Contoh: showPaymentDialog()

            // Panggil fungsi untuk memproses dan menyimpan transaksi
//            processAndSaveTransaction()
            showConfirmationDialog()
        }
    }

    private fun showConfirmationDialog() {
        // Pastikan Anda mengimpor android.app.AlertDialog
        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Transaksi")
            .setMessage("Apakah semua item di keranjang sudah benar?")
            .setPositiveButton("Sudah Benar (OK)") { dialog, which ->
                // Lanjutkan ke langkah pembayaran
                showPaymentDialog()
            }
            .setNegativeButton("Cek Lagi") { dialog, which ->
                dialog.dismiss()
                binding.btnSimpan.isEnabled = true // Pastikan tombol aktif lagi
            }
            .show()
    }

    // TransaksiFragment.kt (Hanya fungsi showPaymentDialog yang diperbaiki)

    private fun showPaymentDialog() {
        val totalGrand = cartItems.sumOf { it.itemPrice * it.quantity }
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply {
            minimumFractionDigits = 0
        }

        // Inflate custom view menggunakan DialogChangeBinding
        val dialogBinding = DialogChangeBinding.inflate(LayoutInflater.from(context))

        // Status Pembayaran lokal
        var cashReceived: Double = 0.0
        var change: Double = 0.0

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setTitle("Pembayaran Tunai")
            .setNegativeButton("Batal") { d, _ -> d.dismiss() }
            .setPositiveButton("Selesai & Cetak") { _, _ -> /* Logika diset di setOnShowListener */ }
            .create()

        // -----------------------------------------------------------
        // 1. Setup Tampilan Awal & Kalkulasi
        // -----------------------------------------------------------

        dialogBinding.tvTotalTagihanValue.text = formatRupiah.format(totalGrand)
        dialogBinding.tvKembalianValue.text = formatRupiah.format(0.0)

        // Warna default (untuk kasus uang pas/kembalian 0)
        dialogBinding.tvKembalianValue.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))

        // Logika kalkulasi utama
        fun updateChangeDisplay(received: Double) {
            cashReceived = received
            change = received - totalGrand

            // Update Kembalian
            dialogBinding.tvKembalianValue.text = formatRupiah.format(change)

            // Update warna kembalian: Hijau jika kembalian positif, Merah jika kurang
            val colorRes = when {
                change > 0 -> R.color.primary // Asumsi Anda punya R.color.green
                change < 0 -> R.color.red_negative   // Asumsi Anda punya R.color.red
                else -> R.color.text_primary // Uang pas atau 0
            }
            dialogBinding.tvKembalianValue.setTextColor(ContextCompat.getColor(requireContext(), colorRes))

            // Update tombol OK
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = change >= 0
        }

        // -----------------------------------------------------------
        // 2. Set Listeners untuk Tombol Quick Select
        // -----------------------------------------------------------

        dialogBinding.btnQuick10k.setOnClickListener { updateChangeDisplay(10000.0) }
        dialogBinding.btnQuick15k.setOnClickListener { updateChangeDisplay(15000.0) }
        dialogBinding.btnQuick20k.setOnClickListener { updateChangeDisplay(20000.0) }
        dialogBinding.btnQuick30k.setOnClickListener { updateChangeDisplay(30000.0) }
        dialogBinding.btnQuick50k.setOnClickListener { updateChangeDisplay(50000.0) }
        dialogBinding.btnQuick100k.setOnClickListener { updateChangeDisplay(100000.0) }

        dialogBinding.btnUangPas.setOnClickListener {
            // Logika Uang Pas: Cash diterima sama dengan total tagihan
            updateChangeDisplay(totalGrand)
        }

        // -----------------------------------------------------------
        // 3. Aksi Positive Button (Proses Simpan)
        // -----------------------------------------------------------

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)

            // Awalnya, non-aktifkan tombol jika belum ada uang diterima atau kurang
            positiveButton.isEnabled = change >= 0

            positiveButton.setOnClickListener {
                if (change >= 0) {
                    // 4. Lanjut ke penyimpanan transaksi
                    processAndSaveTransaction(cashReceived, change)
                    dialog.dismiss()
                } else {
                    Toast.makeText(requireContext(), "Uang diterima kurang dari total belanja.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Inisialisasi default tampilan ke "Uang Pas" (agar tombol "Selesai" aktif jika tidak ada input)
        updateChangeDisplay(totalGrand)

        dialog.show()
    }

    // [BARU] Logika Memproses dan Menyimpan Transaksi
    private fun processAndSaveTransaction(cashReceived: Double, change: Double) {
        binding.btnSimpan.isEnabled = false

        val totalGrand = cartItems.sumOf { it.itemPrice * it.quantity }
        val finalUserId = viewModel.currentUserId.value

        if (cartItems.isEmpty()) {
            Toast.makeText(requireContext(), "Keranjang tidak boleh kosong.", Toast.LENGTH_LONG).show()
            binding.btnSimpan.isEnabled = true // Aktifkan kembali tombol
            Log.e(TAG, "VALIDATION FAILED: Cart is empty.")
            return
        }

        if (finalUserId == null || finalUserId.isBlank()) {
            Toast.makeText(requireContext(), "Sesi kasir tidak valid. Harap login ulang.", Toast.LENGTH_LONG).show()
            binding.btnSimpan.isEnabled = true // Aktifkan kembali tombol
            Log.e(TAG, "VALIDATION FAILED: User ID is null/blank.")
            // TODO: Navigasi paksa ke halaman login jika ini terjadi
            return
        }

        if (totalGrand <= 0.0) {
            Toast.makeText(requireContext(), "Total transaksi harus lebih dari nol.", Toast.LENGTH_LONG).show()
            binding.btnSimpan.isEnabled = true
            Log.e(TAG, "VALIDATION FAILED: Grand total is zero or negative.")
            return
        }

        val subTotal = totalGrand

        Log.d(TAG, "User Id: ${finalUserId.toString()}") // <<< LOG BARU

        // 1. Buat Model NewTransaction
        val newTransaction = NewTransaction(
            userId = finalUserId,
            outletId = "outlet_pusat_001",
            subTotal = subTotal,
            grandTotal = totalGrand,
            note = "", // Catatan opsional
            transactionTime = System.currentTimeMillis(),
            items = cartItems.toList(), // Daftar item dari keranjang

//            cashReceived = cashReceived,
//            change = change
        )

        // 2. Kirim ke ViewModel
        viewModel.recordTransaction(newTransaction)


        // Tampilkan loading/disable tombol
//        binding.btnSimpan.isEnabled = false
    }

    // [BARU] Amati Hasil Penyimpanan
    @SuppressLint("TimberArgCount")
    private fun setupObservers() {
        authViewModel.currentUserId.observe(viewLifecycleOwner) { userId ->
            // Simpan ID yang diperoleh dari ViewModel ke variabel lokal
            currentLoggedInUserId = userId
            Timber.i("TRANSACTION_SESSION", "User ID aktif diperbarui: $userId")
        }

        viewModel.productItems.observe(viewLifecycleOwner) { items ->
            // Asumsikan ini memuat semua ProductItem yang valid dari DB
            loadedProductItems = items
            updateTopProductView(items)
        }

        viewModel.saveTransactionStatus.observe(viewLifecycleOwner) { result ->
            result.onSuccess { transactionId ->
                Toast.makeText(requireContext(), "Transaksi berhasil disimpan! 🎉", Toast.LENGTH_LONG).show()

                // Reset Keranjang
                cartItems.clear()
                updateTotalDisplay()

//                loadAndShowReceipt(transactionId)
                navigateToNotaFragment(transactionId)

                // TODO: Navigasi ke Halaman Nota atau Home
                binding.btnSimpan.isEnabled = true

            }.onFailure {
                Toast.makeText(requireContext(), "Gagal menyimpan: ${it.message}", Toast.LENGTH_LONG).show()
                binding.btnSimpan.isEnabled = true
            }
        }
    }

    /**
     * ✅ FUNGSI BARU: Memicu navigasi ke NotaFragment.
     */
    private fun navigateToNotaFragment(transactionId: String) {
        // Asumsi Anda telah mendefinisikan action di Navigation Graph Anda:
        // action_transaksiFragment_to_notaFragment
        val action = TransaksiFragmentDirections.actionTransaksiFragmentToNotaFragment(transactionId)
        findNavController().navigate(action)
    }

    /**
     * ✅ FUNGSI BARU: Menggunakan logika dari HistoryFragment untuk menampilkan nota.
     */
//    private fun loadAndShowReceipt(transactionId: String) {
//        viewLifecycleOwner.lifecycleScope.launch {
//            // Memanggil fungsi yang sudah ada di TransaksiViewModel
//            val detail = viewModel.getTransactionDetailById(transactionId)
//            if (detail != null) {
//                // Asumsi Anda telah menyalin fungsi showReceiptDialog dari HistoryFragment
//                showReceiptDialog(detail)
//            } else {
//                Toast.makeText(requireContext(), "Nota tidak dapat dimuat.", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

    private fun findAvailableVariants(productName: String): List<ProductItem> {
        // Cari SEMUA ProductItem yang memiliki nama produk yang sama (Ayam Goreng)
        return loadedProductItems.filter { it.name == productName }
    }



//    private fun showReceiptDialog(detail: TransactionDetail) {
//        val dialog = Dialog(requireContext())
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        val dialogBinding = DialogReceiptBinding.inflate(LayoutInflater.from(requireContext()))
//        dialog.setContentView(dialogBinding.root)
//
////        bindReceiptData(dialogBinding, detail.header)
//        val receiptItemAdapter = ReceiptItemAdapter()
//        dialogBinding.rvTransactionItems.apply {
//            layoutManager = LinearLayoutManager(context)
//            adapter = receiptItemAdapter
//        }
//        receiptItemAdapter.submitList(detail.items)
//
//        // Listener Aksi
////        dialogBinding.btnShareReceipt.setOnClickListener {
////            shareReceipt(dialogBinding.root, detail.header.id)
////            dialog.dismiss()
////        }
//
//        // ✅ PICU ALUR CETAK BLUETOOTH
////        dialogBinding.btnPrintReceipt.setOnClickListener {
////            // Tutup dialog nota, lalu mulai cek izin
////            dialog.dismiss()
////            checkBluetoothPermissionsAndStartScan()
////        }
//
//        dialog.show()
//        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//    }

//    private fun bindReceiptData(binding: DialogReceiptBinding, transaction: Transaction) {
//        val transactionDate = Date(transaction.transactionTime)
//        val cashierName = viewModel.currentUserName.value ?: transaction.userId
////        binding.tvTransactionId.text = "Nota: ${transaction.id.take(12)}"
//        binding.tvTransactionId.text = "Nota: #${transaction.id.take(8).uppercase(Locale.ROOT)}"
//        binding.tvTransactionTime.text = "Tanggal: ${datetimeFormatter.format(transactionDate)}"
//        binding.tvCashierName.text = cashierName
//        binding.tvNote.text = "Catatan: ${transaction.note.ifEmpty { "Tidak ada catatan." }}"
//        binding.tvSubtotal.text = currencyFormatter.format(transaction.subTotal)
//        binding.tvGrandTotal.text = currencyFormatter.format(transaction.grandTotal)
//        // TODO: Lookup dan isi tv_cashier_name dan tv_outlet_location
//    }

    // =====================================================================================
    // 🔌 FUNGSI BLUETOOTH & SCANNING
    // =====================================================================================

    /**
     * 1. Cek Izin -> Lanjutkan ke Cek Status Bluetooth
     */
//    private fun checkBluetoothPermissionsAndStartScan() {
//        val permissions = mutableListOf<String>()
//
//        // Izin BLUETOOTH_CONNECT dan BLUETOOTH_SCAN (API 31+)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
//            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
//        }
//        // Izin Lokasi (Wajib untuk scan di API < 31)
//        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
//
//        val requiredPermissions = permissions.filter {
//            ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
//        }
//
//        if (requiredPermissions.isNotEmpty()) {
//            requestBluetoothPermissions.launch(requiredPermissions.toTypedArray())
//        } else {
//            checkBluetoothStatusAndScan() // Lanjut ke langkah 2
//        }
//    }
//
//    private val requestBluetoothPermissions = registerForActivityResult(
//        ActivityResultContracts.RequestMultiplePermissions()
//    ) { permissions ->
//        if (permissions.all { it.value == true }) {
//            checkBluetoothStatusAndScan() // Lanjut setelah izin diberikan
//        } else {
//            Toast.makeText(requireContext(), "Izin Bluetooth atau Lokasi ditolak. Cetak gagal.", Toast.LENGTH_LONG).show()
//        }
//    }
//
//    /**
//     * 2. Cek Status Bluetooth -> Aktifkan jika non-aktif
//     */
//    private fun checkBluetoothStatusAndScan() {
//        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
//        if (bluetoothAdapter == null) {
//            Toast.makeText(requireContext(), "Perangkat tidak mendukung Bluetooth.", Toast.LENGTH_LONG).show()
//        } else if (!bluetoothAdapter.isEnabled) {
//            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//            enableBluetoothLauncher.launch(enableBtIntent)
//        } else {
//            startDeviceScan() // Lanjut ke langkah 3
//        }
//    }
//    private val enableBluetoothLauncher = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        if (result.resultCode == AppCompatActivity.RESULT_OK) {
//            startDeviceScan() // Lanjutkan scan setelah Bluetooth aktif
//        } else {
//            Toast.makeText(requireContext(), "Bluetooth harus diaktifkan untuk mencetak.", Toast.LENGTH_LONG).show()
//        }
//    }


    /**
     * 3. Memulai pemindaian perangkat dan menampilkan dialog
     */
//    private fun startDeviceScan() {
//        Toast.makeText(requireContext(), "Memindai perangkat Bluetooth terdekat...", Toast.LENGTH_SHORT).show()
//
//        // Memicu scan di Use Case
//        printUseCase.startScan()
//
//        // Menampilkan Dialog Pemilihan Printer (Langkah 4)
//        showPrinterSelectionDialog()
//    }

    /**
     * 4. Dialog untuk menampilkan list printer terdekat, dan memicu cetak saat diklik.
     */
//    @SuppressLint("MissingPermission")
//    private fun showPrinterSelectionDialog() {
//        // ... (Logika List Adapter dan Dialog Builder) ...
//        val deviceList = mutableListOf<BluetoothDevice>()
//        val deviceNames = mutableListOf<String>()
//        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, deviceNames)
//
//        val selectionDialog = AlertDialog.Builder(requireContext())
//            .setTitle("Pilih Printer Thermal")
//            .setAdapter(adapter, null)
//            .setNegativeButton("Batal") { d, _ ->
//                printUseCase.stopScan()
//                d.dismiss()
//            }
//            .create()
//
//        // Amati hasil scan dari Use Case
//        printUseCase.scanResults.observe(viewLifecycleOwner) { devices ->
//            deviceList.clear()
//            deviceNames.clear()
//            deviceMap.clear() // Clear map saat ada hasil scan baru
//            deviceList.addAll(devices)
//
//            devices.forEach { device ->
//                val name = device.name ?: "Unnamed Device (${device.address})"
//                deviceNames.add(name)
//                deviceMap[name] = device // Isi deviceMap
//            }
//            adapter.notifyDataSetChanged()
//        }
//
//        // Listener saat printer dipilih
//        selectionDialog.listView.setOnItemClickListener { _, _, position, _ ->
//            printUseCase.stopScan()
//            selectionDialog.dismiss()
//
//            val deviceName = adapter.getItem(position)
//            val selectedDevice = deviceMap[deviceName] // Ambil dari map
//            val detail = transactionDetailToPrint
//
//            if (selectedDevice != null && detail != null) {
//                printViaBluetooth(selectedDevice.address, detail)
//            } else {
//                Toast.makeText(requireContext(), "Data transaksi hilang atau perangkat tidak valid.", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        selectionDialog.show()
//    }

    /**
     * ✅ Fungsi Final: Memicu Use Case cetak Bluetooth.
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
//                Toast.makeText(requireContext(), "Cetak Gagal. Cek koneksi & printer.", Toast.LENGTH_LONG).show()
//            }
//        }
//    }

    // =====================================================================================
    // FUNGSI SHARE & PRINT MANAGER STANDAR
    // =====================================================================================
//
//    private fun shareReceipt(viewRoot: View, transactionId: String) {
//        val shareButton = viewRoot.findViewById<com.google.android.material.button.MaterialButton>(com.praktikum.abstreetfood_management.R.id.btn_share_receipt)
//        val printButton = viewRoot.findViewById<com.google.android.material.button.MaterialButton>(com.praktikum.abstreetfood_management.R.id.btn_print_receipt)
//
//        // Asumsi tombol-tombol berada di parent yang sama, atau Anda bisa sembunyikan container-nya.
//        // Kita sembunyikan langsung:
//        val initialShareVisibility = shareButton.visibility
//        val initialPrintVisibility = printButton.visibility
//
//        shareButton.visibility = View.GONE
//        printButton.visibility = View.GONE
//
//        val bitmap = createBitmapFromView(viewRoot)
//        val file = saveBitmapToCache(bitmap, transactionId)
//        if (file == null) {
//            Toast.makeText(requireContext(), "Gagal menyiapkan file share.", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val uri = FileProvider.getUriForFile(
//            requireContext(),
//            "${requireContext().packageName}.provider",
//            file
//        )
//
//        val shareIntent: Intent = Intent().apply {
//            action = Intent.ACTION_SEND
//            putExtra(Intent.EXTRA_STREAM, uri)
//            type = "image/png"
//            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//        }
//        startActivity(Intent.createChooser(shareIntent, "Bagikan Nota via..."))
//    }
//
//    private fun createBitmapFromView(view: View): Bitmap {
//        view.measure(
//            View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
//            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
//        )
//        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
//
//        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
//        val canvas = Canvas(bitmap)
//        view.draw(canvas)
//        return bitmap
//    }
//
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
//            Log.e(TAG, "Gagal menyimpan bitmap ke cache", e)
//            null
//        }
//    }

    private fun setupHeader() {
        binding.ivBack.setOnClickListener {
            // Jika bottomSheetDialog terlihat, tutup dulu
            if (binding.bottomSheetDialog.visibility == View.VISIBLE) {
                hideVariantSelectionView()
            } else {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun setupCartRecyclerView() {
        cartAdapter = CartAdapter(this)

        binding.rvCartItems.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = cartAdapter
        }
    }

    private fun updateTotalDisplay() {
        val total = cartItems.sumOf { it.itemPrice * it.quantity }
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        binding.tvTotal.text = formatRupiah.format(total)

        // PENTING: Update adapter setiap kali cartItems berubah
        cartAdapter.submitList(cartItems.toList())
    }

    @SuppressLint("TimberArgCount")
    private fun setupTopProductListeners() {
        // Asumsi ProductItem dibuat di sini


        // 1. Klik Ayam Goreng
//        (binding.llTopProducts.getChildAt(0) as? ViewGroup)?.getChildAt(0)?.setOnClickListener {
//            Timber.d("TRANSAKSI_CLICK", "Produk Ayam Goreng diklik. Memanggil view varian.")
//            showVariantSelectionView(ayamGorengDummy)
//        }
//        // 2. Klik Ayam Bakar
//        (binding.llTopProducts.getChildAt(1) as? ViewGroup)?.getChildAt(0)?.setOnClickListener {
//            Timber.d("TRANSAKSI_CLICK", "Produk Ayam Bakar diklik. Memanggil view varian.")
//            showVariantSelectionView(ayamBakarDummy)
//        }
        // 1. Klik Ayam Goreng (Asumsi index 0 di layout adalah Ayam Goreng)
//        (binding.llTopProducts.getChildAt(0) as? ViewGroup)?.getChildAt(0)?.setOnClickListener {
//
//            // Ambil ProductItem pertama yang ada di list (harus di-mapping ke CardView)
//            val itemToUse = loadedProductItems.firstOrNull { it.name.contains("Ayam Goreng") }
//            if (itemToUse != null) {
//                Log.d("ITEM", itemToUse.variantType)
//                showVariantSelectionView(itemToUse)
//            } else {
//                Toast.makeText(requireContext(), "Data Ayam Goreng belum dimuat!", Toast.LENGTH_SHORT).show()
//            }
//        }

        (binding.llTopProducts.getChildAt(0) as? ViewGroup)?.getChildAt(0)?.setOnClickListener {
            val clickedProductName = "Ayam Goreng"

            // ✅ KOREKSI: Ambil SEMUA ProductItem yang bernama "Ayam Goreng"
            val allVariantsOfProduct = loadedProductItems.filter { it.name == clickedProductName }

            if (allVariantsOfProduct.isNotEmpty()) {
                // Kita kirim varian default (Nasi Biasa) sebagai patokan untuk tampilan awal
                val defaultItem = allVariantsOfProduct.firstOrNull { it.variantType == "NASI_BIASA" }
                    ?: allVariantsOfProduct.first() // Fallback ke yang pertama ditemukan

                // ✅ Simpan SEMUA varian ke map untuk lookup harga
                availableVariantsMap = allVariantsOfProduct.associateBy { it.variantType }

                Log.d(TAG, "VALIDASI: Produk '$clickedProductName' ditemukan. Total Varian: ${allVariantsOfProduct.size}")
                showVariantSelectionView(defaultItem)
            } else {
                Toast.makeText(requireContext(), "Data varian Ayam Goreng belum dimuat!", Toast.LENGTH_SHORT).show()
            }
        }

        // 2. Klik Ayam Bakar
        (binding.llTopProducts.getChildAt(1) as? ViewGroup)?.getChildAt(0)?.setOnClickListener {
            val clickedProductName = "Ayam Bakar"
            val allVariantsOfProduct = loadedProductItems.filter { it.name == clickedProductName }

            if (allVariantsOfProduct.isNotEmpty()) {
                val defaultItem = allVariantsOfProduct.firstOrNull { it.variantType == "NASI_BIASA" }
                    ?: allVariantsOfProduct.first()

                availableVariantsMap = allVariantsOfProduct.associateBy { it.variantType }
                Log.d(TAG, "VALIDASI: Produk '$clickedProductName' ditemukan. Total Varian: ${allVariantsOfProduct.size}")
                showVariantSelectionView(defaultItem)
            } else {
                Toast.makeText(requireContext(), "Data varian Ayam Bakar belum dimuat!", Toast.LENGTH_SHORT).show()
            }
        }

//        // 2. Klik Ayam Bakar (Asumsi index 1 di layout adalah Ayam Bakar)
//        (binding.llTopProducts.getChildAt(1) as? ViewGroup)?.getChildAt(0)?.setOnClickListener {
//            val itemToUse = loadedProductItems.firstOrNull { it.name.contains("Ayam Bakar") }
//            if (itemToUse != null) {
//                Log.d("ITEM", itemToUse.variantType)
//                showVariantSelectionView(itemToUse)
//            } else {
//                Toast.makeText(requireContext(), "Data Ayam Bakar belum dimuat!", Toast.LENGTH_SHORT).show()
//            }
//        }
    }

    private fun findVariantItem(productName: String, variantType: String): ProductItem? {
        // Cari item di list yang sudah dimuat yang cocok dengan Nama Produk DAN Tipe Varian
        return loadedProductItems.firstOrNull {
            it.name == productName && it.variantType == variantType
        }
    }

    private fun updateTopProductView(items: List<ProductItem>) {
        // Logika untuk mengisi CardView hardcoded Anda dengan data items yang valid
        // (Misalnya, mengisi harga, nama, dll. menggunakan data dari items)
    }

    private fun setupVariantViewListeners() {
        // Listener untuk kalkulasi real-time saat RadioButton diubah
//        binding.rgVarian.setOnCheckedChangeListener { _, checkedId ->
//            Log.d(TAG, "selected PI : ${selectedProductItem}")
//            val selectedTag = view?.findViewById<RadioButton>(checkedId)?.tag as? String ?: "TANPA_NASI"
//            selectedProductItem?.let { item ->
//                calculateAndDisplayPrice(item, selectedTag)
//            }
//        }

        binding.rgVarian.setOnCheckedChangeListener { _, checkedId ->

            val selectedTag = view?.findViewById<RadioButton>(checkedId)?.tag as? String ?: return@setOnCheckedChangeListener
            val selectedItemByVariant = availableVariantsMap[selectedTag]

            // ✅ GANTI: Gunakan nama produk dari item yang disimpan saat ini
            val currentProductName = selectedProductItem?.name ?: return@setOnCheckedChangeListener

            // Panggil fungsi untuk mendapatkan ProductItem yang benar dari map varian

            selectedItemByVariant?.let { item ->
                // ✅ KOREKSI KRITIS: Kirim item varian yang BENAR (bukan hanya item default)
                calculateAndDisplayPrice(item, selectedTag)
            }

        }

        // Listener Tombol "Tambahkan ke Keranjang"
        binding.btnAddToCart.setOnClickListener {
            addToCartFromVariantView()
        }
    }

//    private fun showVariantSelectionView(item: ProductItem) {
//        selectedProductItem = item // Simpan data item yang sedang aktif
//
//        // Tampilkan View dan Animasi
//        binding.overlayView.visibility = View.VISIBLE
//        binding.bottomSheetDialog.visibility = View.VISIBLE
//        binding.bottomSheetDialog.animate()
//            .translationY(0f)
//            .setDuration(300)
//            .start()
//
//        // Isi data dan kalkulasi
//        binding.tvVariantTitle.text = "Pilih Varian untuk ${item.name}"
//        binding.rgVarian.clearCheck()
//        binding.rbNasiBiasa.isChecked = true
//        calculateAndDisplayPrice(item, "NASI_BIASA")
//
//        Log.i("VARIANT_VIEW", "View Varian ditampilkan untuk produk: ${item.name}")
//    }

    private fun showVariantSelectionView(item: ProductItem) {
        // Cari semua varian untuk nama produk ini (misalnya, Ayam Goreng)
        val allVariants = findAvailableVariants(item.name)

        // ✅ SIMPAN SEMUA VARIAN KE MAP BARU (Key: variantType, Value: ProductItem)
        // Ini adalah kunci agar kita bisa mencari harga yang benar
        availableVariantsMap = allVariants.associateBy { it.variantType }

        // Asumsi: Varian default yang pertama kali dipilih adalah NASI_BIASA
        val defaultVariant = availableVariantsMap["NASI_BIASA"] ?: item

        selectedProductItem = defaultVariant // Simpan item yang aktif (misalnya AG_NASI_BIASA)

        // ... (Logika tampilan dan animasi) ...
        binding.overlayView.visibility = View.VISIBLE
        binding.bottomSheetDialog.visibility = View.VISIBLE
        binding.bottomSheetDialog.animate()
            .translationY(0f)
            .setDuration(300)
            .start()

        binding.tvVariantTitle.text = "Pilih Varian untuk ${item.name}"

        // Atur default selection dan harga
        binding.rgVarian.clearCheck()
        binding.rbNasiBiasa.isChecked = true
        calculateAndDisplayPrice(defaultVariant, "NASI_BIASA") // Hitung harga item NASI_BIASA
    }

    private fun hideVariantSelectionView() {
        binding.bottomSheetDialog.animate()
            .translationY(binding.bottomSheetDialog.height.toFloat())
            .setDuration(300)
            .withEndAction {
                binding.bottomSheetDialog.visibility = View.GONE
                binding.overlayView.visibility = View.GONE
                selectedProductItem = null
            }
            .start()
    }

    private fun addToCartFromVariantView() {
        val currentItem = selectedProductItem ?: return

        val selectedRadioButtonId = binding.rgVarian.checkedRadioButtonId
        val selectedRadioButton = view?.findViewById<RadioButton>(selectedRadioButtonId)

        if (selectedRadioButton == null) {
            Toast.makeText(requireContext(), "Pilih varian terlebih dahulu.", Toast.LENGTH_SHORT).show()
            return
        }

        val variantTag = selectedRadioButton.tag as String

        val variantNameDisplay = selectedRadioButton.text.toString()
        val productName = currentItem.name.toString()

        val finalProductItem = availableVariantsMap[variantTag] ?: return
//        val finalPrice = calculatePrice(currentItem, variantTag)
        val finalPrice = finalProductItem.sellingPrice
        Log.d("ITEM", finalPrice.toString())

        Log.d(TAG, "ID Item: ${currentItem.id}") // <<< LOG BARU

        // 1. Buat NewTransactionItem
        val newItem = NewTransactionItem(
//            productItemId = currentItem.id,
            productItemId = finalProductItem.id,
            productName = productName,
            variantName = variantNameDisplay,
            quantity = 1,
            itemPrice = finalPrice,
//            isExtraRice = variantTag.contains("NASI")
        )

        // 2. Kirim ke fungsi penambahan keranjang (dengan logika penggabungan)
        onVariantItemAdded(newItem)

        // 3. Sembunyikan View
        hideVariantSelectionView()
    }

    private fun calculatePrice(item: ProductItem, variantKey: String): Double {
//        val modifier = variantModifiers[variantKey] ?: 0.0
//        Log.d("VARIANT_VIEW"," ${item.sellingPrice} + ${modifier}")
//        return item.sellingPrice + modifier

        val finalItem = availableVariantsMap[variantKey]

        // Jika item varian ditemukan, kembalikan harga jualnya (sellingPrice)
        return finalItem?.sellingPrice ?: 0.0
    }

    private fun calculateAndDisplayPrice(item: ProductItem, variantKey: String) {
//        val newPrice = calculatePrice(item, variantKey)
        val newPrice = item.sellingPrice
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

        binding.tvCalculatedPrice.text = formatRupiah.format(newPrice)

        Log.d(TAG, "DISPLAY PRICE: Varian=$variantKey, Price=$newPrice")
    }

    // --- IMPLEMENTASI VARIANT SELECTION LISTENER (Logika Penggabungan) ---
    @SuppressLint("TimberArgCount")
    override fun onVariantItemAdded(item: NewTransactionItem) {

        // 1. Cek apakah item dengan ID dan Harga yang sama sudah ada di keranjang
        val existingItemIndex = cartItems.indexOfFirst {
            // Kita gunakan ProductItemId (ID Varian) DAN itemPrice sebagai kunci unik
            it.productItemId == item.productItemId && it.itemPrice == item.itemPrice
        }

        if (existingItemIndex != -1) {
            // 2. KONDISI DIPENUHI (Item sama): Naikkan kuantitas item yang sudah ada
            val existingItem = cartItems[existingItemIndex]

            val updatedItem = existingItem.copy(
                quantity = existingItem.quantity + item.quantity
            )

            cartItems[existingItemIndex] = updatedItem
            Toast.makeText(requireContext(), "Kuantitas ditambahkan (${updatedItem.quantity}x).", Toast.LENGTH_SHORT).show()
            Timber.i("CART_MERGE", "Item ${item.productItemId} quantity updated to ${updatedItem.quantity}")

        } else {
            // 3. KONDISI TIDAK DIPENUHI: Tambahkan item baru ke keranjang
            cartItems.add(item)
            Toast.makeText(requireContext(), "Item baru ditambahkan.", Toast.LENGTH_SHORT).show()
            Timber.i("CART_MERGE", "New item ${item.productItemId} added to cart.")
        }

        updateTotalDisplay()
    }

    // --- IMPLEMENTASI CART ACTION LISTENER (Dari Adapter) ---
    override fun invoke(item: NewTransactionItem, action: CartAction) {
        // Cari item berdasarkan ID dan Harga (karena item baru di keranjang bisa memiliki kuantitas > 1)
        val index = cartItems.indexOfFirst {
            it.productItemId == item.productItemId && it.itemPrice == item.itemPrice
        }
        if (index == -1) return

        when (action) {
            CartAction.ADD -> {
                val updatedItem = item.copy(quantity = item.quantity + 1)
                cartItems[index] = updatedItem
            }
            CartAction.REMOVE -> {
                if (item.quantity > 1) {
                    val updatedItem = item.copy(quantity = item.quantity - 1)
                    cartItems[index] = updatedItem
                } else {
                    // Hapus item jika kuantitas mencapai 0
                    cartItems.removeAt(index)
                }
            }
            CartAction.DELETE -> {
                cartItems.removeAt(index)
            }
        }
        updateTotalDisplay()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}