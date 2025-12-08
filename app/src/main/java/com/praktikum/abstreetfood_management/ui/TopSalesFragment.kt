package com.praktikum.abstreetfood_management.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.praktikum.abstreetfood_management.data.adapter.ListProductAdapter
import com.praktikum.abstreetfood_management.data.adapter.TopSellingAdapter
import com.praktikum.abstreetfood_management.databinding.FragmentHomeBinding
import com.praktikum.abstreetfood_management.databinding.FragmentListProductBinding
import com.praktikum.abstreetfood_management.databinding.FragmentTopSalesBinding
import com.praktikum.abstreetfood_management.domain.model.ProductItem
import com.praktikum.abstreetfood_management.domain.model.TopSellingProduct

class TopSalesFragment : Fragment(){
    private var _binding: FragmentTopSalesBinding? = null
    private val binding get() = _binding!!

    private lateinit var topSellingAdapter: TopSellingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTopSalesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadData()
    }

    private fun setupRecyclerView() {
        // Adapter dengan lambda untuk menangani klik
        topSellingAdapter = TopSellingAdapter { item ->
            handleProductClick(item)
        }

        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = topSellingAdapter
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
            .setTitle("Konfirmasi Tambah")
            .setMessage("Tambahkan '${item.name}' dengan harga Rp.${item.totalRevenue} ke transaksi?") //kayanya harus kirim product item id deh
            .setPositiveButton("Ya, Tambahkan") { dialog, _ ->
                // TODO: Panggil Use Case untuk mencatat NewTransactionItem default
                Toast.makeText(requireContext(), "${item.name} ditambahkan!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
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

    private fun loadData() {
        // CONTOH: Mengisi data List Product (menggunakan data dummy sementara)
//        val dummyData = listOf(
//
////            ProductItem("id1", "P001", "Ayam Goreng", 12000.0,  "NASI_BIASA"),
////            ProductItem("id2", "P001", "Ayam Bakar", 12000.0,  "TANPA_NASI"),
//            // ... Tambahkan lebih banyak data dummy ProductItem
//        )
//        val dummyData // ganti data ambil dari top salles di database
//        topSellingAdapter.submitList(dummyData)


        // TODO: Ganti dengan observer ViewModel saat sudah siap:
        // viewModel.topSellingProducts.observe(viewLifecycleOwner) { products ->
        //     listProductAdapter.submitList(products)
        // }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}