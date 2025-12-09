package com.praktikum.abstreetfood_management.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import com.praktikum.abstreetfood_management.R
//import com.praktikum.abstreetfood_management.data.adapter.StockAdapter
import com.praktikum.abstreetfood_management.data.adapter.TabPageAdapter
//import com.praktikum.abstreetfood_management.data.adapter.TopSellingAdapter
import com.praktikum.abstreetfood_management.databinding.FragmentHomeBinding
import com.praktikum.abstreetfood_management.domain.model.DailyMetric
import com.praktikum.abstreetfood_management.viewmodel.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // [BARU] Inject ViewModel
//    @Inject
    private val viewModel: DashboardViewModel by viewModels()

    // [BARU] Dua Adapter yang berbeda
//    private lateinit var topSellingAdapter: TopSellingAdapter
//    private lateinit var stockAdapter: StockAdapter

    private val tabTitles = arrayListOf("Top Sales Product", "History")
//    private val tabTitles = mutableMapOf("Top Sales Product" to R.drawable, "History")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    private fun setUpTabLayoutWithViewPager() {
//        binding.viewPager.adapter = TabPageAdapter(this)

        val adapter = TabPageAdapter(this)
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabs, binding.viewPager) {tab,position->
//            tab.text = tabTitles[position]
            tab.text = adapter.getTabTitle(position)
        }.attach()

//        for (i in 0..2){
        for (i in 0 until adapter.itemCount) {
            val textView = LayoutInflater.from(requireContext()).inflate(R.layout.tab_title, null)
                as TextView
            binding.tabs.getTabAt(i)?.customView = textView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpTabLayoutWithViewPager()
        val parentNavController = findNavController()

        // Setup Bottom Navigation
//        val bottomNavView = binding.bottomNavigation // Menggunakan binding karena Anda sudah setup ViewBinding
        // This links the BottomNavigationView item IDs (from bottom_nav_menu.xml)
        // to the destinations in nav_graph.xml using the parentNavController.
        // Note: nav_home, nav_transaksi, nav_laporan, nav_profile IDs must match
        // the destination IDs in nav_graph.xml (homeFragment, transaksiFragment, etc.)
//        bottomNavView.setupWithNavController(parentNavController)

        // Handle the menu icon click to open the drawer
        binding.ivMenu.setOnClickListener { // Menggunakan binding
            // Ambil DrawerLayout dari Activity dan buka
            (requireActivity().findViewById<DrawerLayout>(R.id.drawerLayout)).openDrawer(
                GravityCompat.START)
        }
        // Handle the profile icon click
        binding.ivProfile.setOnClickListener { // Menggunakan binding
            parentNavController.navigate(R.id.profileFragment)
        }

        setupAdapters() // <<< Setup Adapter Awal
//        setupTabListeners() // <<< Setup Listener Tab
        setupObservers()
    }

    private fun setupAdapters() {
        // Inisialisasi Adapter Penjualan Teratas
//        topSellingAdapter = TopSellingAdapter { product ->
//            // Logika klik: Tampilkan detail/pilihan tambah transaksi produk terlaris
//        }
//
//        // Inisialisasi Adapter Stok
//        stockAdapter = StockAdapter { stockItem ->
//            // Logika klik: Tampilkan detail/pilihan untuk mencatat bahan masuk (restock)
//        }

        // Atur RecyclerView untuk menggunakan adapter default (Penjualan Teratas)
//        binding.rvProducts.layoutManager = LinearLayoutManager(context)
//        binding.rvProducts.adapter = topSellingAdapter // Default: Penjualan Teratas
    }

//    private fun setupTabListeners() {
//        // Logika saat klik Tab Penjualan Teratas
//        binding.tvPenjualanTab.setOnClickListener {
//            selectTab(isTopSelling = true)
//        }
//
//        // Logika saat klik Tab Stok
//        binding.tvStokTab.setOnClickListener {
//            selectTab(isTopSelling = false)
//        }
//    }

//    private fun selectTab(isTopSelling: Boolean) {
//        if (isTopSelling) {
//            // A. Tampilkan Penjualan Teratas
//            binding.rvProducts.adapter = topSellingAdapter
//            topSellingAdapter.submitList(viewModel.topSellingProducts.value)
//
//            // B. Perbarui Tampilan Tab (Warna)
//            binding.tvPenjualanTab.setBackgroundResource(R.drawable.tab_selected)
//            binding.tvStokTab.setBackgroundResource(android.R.color.transparent)
//            binding.tvStokTab.setTextColor(requireContext().getColor(R.color.primary_teal))
//            binding.tvPenjualanTab.setTextColor(requireContext().getColor(R.color.text_primary))
//        } else {
//            // A. Tampilkan Stok
//            binding.rvProducts.adapter = stockAdapter
//            stockAdapter.submitList(viewModel.stockItems.value)
//
//            // B. Perbarui Tampilan Tab (Warna)
//            binding.tvStokTab.setBackgroundResource(R.drawable.tab_selected)
//            binding.tvPenjualanTab.setBackgroundResource(android.R.color.transparent)
//            binding.tvPenjualanTab.setTextColor(requireContext().getColor(R.color.primary_teal))
//            binding.tvStokTab.setTextColor(requireContext().getColor(R.color.text_primary))
//        }
//    }

    private fun setupObservers() {

        viewModel.dailyTransactionMetrics.observe(viewLifecycleOwner) { metrics ->
            updateTransactionCard(metrics)
        }

        // 2. Amati Metrik Pendapatan
        viewModel.dailyRevenueMetrics.observe(viewLifecycleOwner) { metrics ->
            updateRevenueCard(metrics)
        }

        // 1. Amati Total Pendapatan Harian
//        viewModel.dailyRevenue.observe(viewLifecycleOwner) { revenue ->
//            val formattedRevenue = NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(revenue ?: 0.0)
//            binding.tvTotalPendapatan.text = formattedRevenue
//        }

        // 2. Amati Produk Terlaris (Untuk di-cache dan digunakan saat tab aktif)
//        viewModel.topSellingProducts.observe(viewLifecycleOwner) { products ->
//            // Hanya update adapter jika tab Penjualan Teratas sedang aktif
//            if (binding.tvPenjualanTab.background.constantState == resources.getDrawable(R.drawable.tab_selected).constantState) {
//                topSellingAdapter.submitList(products)
//            }
//        }
//
//        // 3. Amati Stok Items (Untuk di-cache dan digunakan saat tab aktif)
//        viewModel.stockItems.observe(viewLifecycleOwner) { items ->
//            // Hanya update adapter jika tab Stok sedang aktif
//            if (binding.tvStokTab.background.constantState == resources.getDrawable(R.drawable.tab_selected).constantState) {
//                stockAdapter.submitList(items)
//            }
//        }

        // TODO: Anda perlu observer untuk Total Transaksi (tvTotalTransaksi)
    }

    // ✅ FUNGSI BARU: Update Kartu Transaksi
    private fun updateTransactionCard(metrics: DailyMetric) {
        val count = metrics.currentTotal.toInt()
        val percentage = metrics.percentageChange
        val isPositive = percentage >= 0
        val percentageText = if (isPositive) "+${"%.1f".format(percentage)}%" else "${"%.1f".format(percentage)}%"

        // Update Total Transaksi
        binding.tvTotalTransaksi.text = count.toString()
        binding.tvTransaksiPercentage.text = percentageText

        // Update Warna Persentase dan Lingkaran
        val color = if (isPositive) R.color.green_positive else R.color.red_negative // Asumsi R.color.red_negative
        val drawable = if (isPositive) R.drawable.circle_green else R.drawable.circle_red // Asumsi R.drawable.circle_red

        binding.tvTransaksiPercentage.setTextColor(ContextCompat.getColor(requireContext(), color))
        binding.tvTransaksiPercentage.text = percentageText

        // Update Lingkaran
        binding.viewCircleTransaksi.setBackgroundResource(drawable)
//        binding.root.findViewById<View>(R.id.circle_green).setBackgroundResource(drawable)
    }

    // ✅ FUNGSI BARU: Update Kartu Pendapatan
    private fun updateRevenueCard(metrics: DailyMetric) {
        val revenue = metrics.currentTotal
        val percentage = metrics.percentageChange
        val isPositive = percentage >= 0
        val percentageText = if (isPositive) "+${"%.1f".format(percentage)}%" else "${"%.1f".format(percentage)}%"
        val formattedRevenue = NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(revenue)

        // Update Total Pendapatan
        binding.tvTotalPendapatan.text = formattedRevenue
        binding.tvPendapatanPercentage.text = percentageText

        // Update Warna Persentase dan Lingkaran
        val color = if (isPositive) R.color.green_positive else R.color.red_negative
        val drawable = if (isPositive) R.drawable.circle_green else R.drawable.circle_red

        binding.tvPendapatanPercentage.setTextColor(ContextCompat.getColor(requireContext(), color))
        binding.tvPendapatanPercentage.text = percentageText

        // Update Lingkaran (Asumsi ID lingkaran di layout Anda)
        // Note: Karena Anda tidak memberikan ID spesifik pada View Lingkaran di Card 2,
        // saya akan menggunakan findViewById pada parent ViewGroup jika diperlukan
        // Untuk saat ini, asumsikan View pertama di LinearLayout adalah lingkaran.
        val container = binding.tvPendapatanPercentage.parent as ViewGroup
        container.getChildAt(0).setBackgroundResource(drawable)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}