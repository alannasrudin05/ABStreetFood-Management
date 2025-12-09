package com.praktikum.abstreetfood_management.ui

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.praktikum.abstreetfood_management.data.adapter.ListProductAdapter
import com.praktikum.abstreetfood_management.databinding.FragmentHomeBinding
import com.praktikum.abstreetfood_management.databinding.FragmentListProductBinding
import com.praktikum.abstreetfood_management.domain.model.ProductItem
import com.praktikum.abstreetfood_management.viewmodel.InventoryViewModel
import androidx.fragment.app.viewModels
import com.praktikum.abstreetfood_management.domain.model.TopSellingProduct
import com.praktikum.abstreetfood_management.viewmodel.TransaksiViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListProductFragment : Fragment(){
    private var _binding: FragmentListProductBinding? = null
    private val binding get() = _binding!!

    private lateinit var listProductAdapter: ListProductAdapter
//    private val viewModel: InventoryViewModel by viewModels()
    private val viewModel: TransaksiViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadData()
    }

    private fun setupRecyclerView() {
        // Adapter dengan lambda untuk menangani klik
//        listProductAdapter = ListProductAdapter { item ->
//            handleProductClick(item)
//        }

        listProductAdapter = ListProductAdapter { item ->
            // item di sini adalah TopSellingProduct
            handleProductClick(item)
        }

        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listProductAdapter
        }
    }

    // Fungsi yang menangani klik item
    private fun handleProductClick(item: TopSellingProduct) {
        // Tampilkan BottomSheetDialog untuk pemilihan varian (metode yang lebih kompleks)
//        showVariantSelectionDialog(item)

        // JIKA ANDA INGIN MENGGUNAKAN ALERT DIALOG SEDERHANA, gunakan ini:
         showSimpleConfirmationDialog(item)
    }

    // Metode 1: AlertDialog Sederhana (Konfirmasi)
    private fun showSimpleConfirmationDialog(item: TopSellingProduct) {
        AlertDialog.Builder(requireContext())
            .setTitle("Detail Top Sales")
            // Menggunakan data Top Sales: Nama, Kuantitas, dan Revenue
            .setMessage("Produk Terlaris Hari Ini:\n${item.name}\nTerjual: ${item.totalQuantitySold} item\nRevenue: Rp.${item.totalRevenue}")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton("Tutup") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    // Metode 2: BottomSheetDialogFragment (Varian & Kalkulasi)
//    private fun showVariantSelectionDialog(item: ProductItem) {
//        // Mengirim data ProductItem yang diklik ke Dialog Fragment
//        val dialog = ProductVariantDialog.newInstance(item)
//        dialog.show(parentFragmentManager, ProductVariantDialog.TAG)
//    }

//    private fun loadData() {
        // CONTOH: Mengisi data List Product (menggunakan data dummy sementara)
//        val dummyData = listOf(
//            ProductItem("id1", "P001", "Ayam Goreng", 12000.0,  "NASI_BIASA"),
//            ProductItem("id2", "P001", "Ayam Bakar", 12000.0,  "TANPA_NASI"),
//            // ... Tambahkan lebih banyak data dummy ProductItem
//        )
//        listProductAdapter.submitList(dummyData)
//        viewModel.allProductItems.observe(viewLifecycleOwner) { products ->
//            if (products.isEmpty()) {
//                Toast.makeText(requireContext(), "Tidak ada data produk yang tersedia.", Toast.LENGTH_SHORT).show()
//            }
//            listProductAdapter.submitList(products)
//        }

        // TODO: Ganti dengan observer ViewModel saat sudah siap:
        // viewModel.topSellingProducts.observe(viewLifecycleOwner) { products ->
        //     listProductAdapter.submitList(products)
        // }
//    }

    fun loadData() {
        Log.d("DATA_PREP", "Memulai pengecekan kesiapan data di ListProductFragment.")

        // 1. Target Data Yang Diinginkan
        // Tujuan: Menampilkan Top Selling Products.
        // Model Data: TopSellingProduct (name, totalQuantitySold, totalRevenue).
        Log.d("DATA_PREP", "Target model data: TopSellingProduct (mengandung Total Penjualan & Revenue).")

        // 2. Aktifkan Observer dan kirim data ke Adapter
        viewModel.topSellingProducts.observe(viewLifecycleOwner) { topSalesProducts ->
            Log.d("DATA_PREP", "Observer TopSellingProducts aktif. Jumlah item diterima: ${topSalesProducts.size}")

            if (topSalesProducts.isNotEmpty()) {
                // PENTING: BARIS INI AKAN MENYEBABKAN 'Type mismatch' JIKA ListProductAdapter
                // TIDAK DIUBAH EKSTERNAL UNTUK MENERIMA TopSellingProduct
                // Saya anggap Anda akan segera mengubah ListProductAdapter agar kompatibel.
                listProductAdapter.submitList(topSalesProducts)

                Log.i("DATA_PREP", "Data Top Sales sukses disubmit ke Adapter.")
            } else {
                Log.w("DATA_PREP", "Tidak ada data Top Sales ditemukan untuk hari ini.")
                Toast.makeText(requireContext(), "Tidak ada data produk terlaris hari ini.", Toast.LENGTH_SHORT).show()
            }
        }

        // Log ini sudah tidak relevan karena observer sudah diaktifkan.
        // Log.w("DATA_PREP", "Status ViewModel Observer: Observer untuk 'topSellingProducts' saat ini dinonaktifkan (dikomentari/TODO).")
        Log.d("DATA_PREP", "Catatan: Data yang disubmit adalah TopSellingProduct (hasil agregasi).")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}