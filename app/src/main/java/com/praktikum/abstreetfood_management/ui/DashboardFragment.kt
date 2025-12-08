package com.praktikum.abstreetfood_management.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.praktikum.abstreetfood_management.data.adapter.TopSellingAdapter
import com.praktikum.abstreetfood_management.databinding.FragmentDashboardBinding
import com.praktikum.abstreetfood_management.domain.model.User
import com.praktikum.abstreetfood_management.viewmodel.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by viewModels() // Asumsi Anda punya DashboardViewModel
    private lateinit var topProductAdapter: TopSellingAdapter // Adapter untuk produk terlaris

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    private fun setupRecyclerView() {
        topProductAdapter = TopSellingAdapter{

        }
        binding.rvTopSellingProducts.apply {
            layoutManager = LinearLayoutManager(context)
//            adapter = topProductAdapter
        }

    }

    private fun setupListeners() {
        // Navigasi ke LaporanFragment/AnalisisFragment
        binding.btnViewFullReport.setOnClickListener {
            // Asumsi role owner/admin bisa navigasi ke Laporan (Analisis)
            // findNavController().navigate(R.id.action_dashboardFragment_to_laporanFragment)
        }
    }

    private fun setupObservers() {
        // Amati Role user untuk menyesuaikan tampilan
//        viewModel.currentUserId.observe(viewLifecycleOwner) { user ->
//            if (user != null) {
//                updateUiBasedOnRole(user.)
//            }
//        }

        val userRole = viewModel.currentUserRole.value
        updateUiBasedOnRole(userRole.toString())

        // Amati Data Metrik
        viewModel.dailyRevenue.observe(viewLifecycleOwner) { revenue ->
            val formattedRevenue = formatRupiah(revenue ?: 0.0)
            binding.tvTotalRevenue.text = formattedRevenue
        }

        viewModel.dailyTransactionCount.observe(viewLifecycleOwner) { count ->
            binding.tvTotalTransactions.text = (count ?: 0).toString()
        }

        // Amati Produk Terlaris
        viewModel.topSellingProducts.observe(viewLifecycleOwner) { products ->
            topProductAdapter.submitList(products)
        }
    }

    private fun updateUiBasedOnRole(user: String) {
        if (user == "Kasir") {
            binding.toolbar.title = "Dashboard Pegawai "
            // Pegawai hanya melihat statistik pribadi/shift
            binding.btnViewFullReport.visibility = View.GONE
        } else {
            binding.toolbar.title = "Dashboard Owner/Admin"
            // Owner/Admin melihat statistik Outlet, dan tombol Laporan aktif
            binding.btnViewFullReport.visibility = View.VISIBLE
        }
    }

    // Helper function untuk format mata uang
    private fun formatRupiah(number: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        return format.format(number)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


//// ui/DashboardFragment.kt (Menggantikan file lama)
//package com.praktikum.abstreetfood_management.ui
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.praktikum.abstreetfood_management.databinding.FragmentDashboardBinding
//import com.praktikum.abstreetfood_management.viewmodel.DashboardViewModel
//import dagger.hilt.android.AndroidEntryPoint
//import java.text.NumberFormat
//import java.util.Locale
//
//// [BARU] Tambahkan @AndroidEntryPoint untuk Hilt
//@AndroidEntryPoint
//class DashboardFragment : Fragment() {
//
//    // [BARU] View Binding
//    private var _binding: FragmentDashboardBinding? = null
//    private val binding get() = _binding!!
//
//    // [BARU] Inject ViewModel
//    private val viewModel: DashboardViewModel by viewModels()
////    private lateinit var topSellingAdapter: TopSellingAdapter // Inisialisasi Adapter
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
//
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
////        setupRecyclerView()
////        setupObservers()
////        setupListeners()
//    }
//
////    private fun setupRecyclerView() {
////        topSellingAdapter = TopSellingAdapter { product ->
////            // TODO: Logika saat item produk di list diklik
////            // Navigasi atau tampilkan dialog detail produk.
////            // Dari sini user bisa klik 'Tambah Transaksi'
////        }
////        binding.rvDashboardList.apply { // Asumsi Anda punya RecyclerView di layout
////            layoutManager = LinearLayoutManager(context)
////            adapter = topSellingAdapter
////        }
////    }
//
////    private fun setupListeners() {
////        // [BARU] Listener untuk tombol Penjualan Teratas
////        binding.btnTopSelling.setOnClickListener {
////            // TODO: Tampilkan list produk terlaris di bawah (RecyclerView)
////        }
////
////        // [BARU] Listener untuk tombol Stok
////        binding.btnStock.setOnClickListener {
////            // TODO: Tampilkan list stok (dengan total Nasi terpisah) di bawah
////        }
////
////        // Listener untuk Total Pendapatan/Transaksi (untuk detail)
////        binding.cardTotalRevenue.setOnClickListener {
////            // TODO: Navigasi ke detail transaksi harian
////        }
////    }
////
////    private fun setupObservers() {
////        // Amati Total Pendapatan Harian
////        viewModel.dailyRevenue.observe(viewLifecycleOwner) { revenue ->
////            val formattedRevenue = NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(revenue ?: 0.0)
////            binding.tvTotalPendapatan.text = formattedRevenue // Asumsi Anda memiliki ID tvTotalPendapatan di layout
////        }
////
////        // Amati Produk Terlaris
////        viewModel.topSellingProducts.observe(viewLifecycleOwner) { products ->
////            // TODO: Update RecyclerView untuk menampilkan products
////            // binding.rvProductList.adapter = TopSellingAdapter(products)
////        }
////
////        // Amati Total Stok
////        viewModel.totalStock.observe(viewLifecycleOwner) { stock ->
////            // TODO: Update UI dengan total stok jika diperlukan, atau hanya untuk tombol Stock
////        }
////    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}