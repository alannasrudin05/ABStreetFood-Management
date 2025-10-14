package com.praktikum.abstreetfood_management

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    private var tvTotalTransaksi: TextView? = null
    private var tvTotalPendapatan: TextView? = null
    private var tvTotalPengeluaran: TextView? = null
    private var tvPenjualanTab: TextView? = null
    private var tvStokTab: TextView? = null
    private var rvProducts: RecyclerView? = null
    private var bottomNavigation: BottomNavigationView? = null
    private var productAdapter: ProductAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initViews()
        setupRecyclerView()
        setupBottomNavigation()
        setupTabs()
        loadDummyData()
    }

    private fun initViews() {
        tvTotalTransaksi = findViewById(R.id.tvTotalTransaksi)
        tvTotalPendapatan = findViewById(R.id.tvTotalPendapatan)
        tvTotalPengeluaran = findViewById(R.id.tvTotalPengeluaran)
        tvPenjualanTab = findViewById(R.id.tvPenjualanTab)
        tvStokTab = findViewById(R.id.tvStokTab)
        rvProducts = findViewById(R.id.rvProducts)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        // Tambahkan click listener untuk hamburger menu (sementara untuk testing)
        findViewById<View>(R.id.ivMenu).setOnClickListener { v: View? ->
            // Navigate to Inventaris
            startActivity(
                Intent(
                    this,
                    InventarisActivity::class.java
                )
            )
        }
    }

    private fun setupRecyclerView() {
        rvProducts!!.layoutManager = LinearLayoutManager(this)
        productAdapter = ProductAdapter(this)
        rvProducts!!.adapter = productAdapter
    }

    private fun setupBottomNavigation() {
        bottomNavigation!!.selectedItemId = R.id.nav_home
        bottomNavigation!!.setOnItemSelectedListener { item: MenuItem ->
            val itemId = item.itemId
            if (itemId == R.id.nav_home) {
                // Already on home
                return@setOnItemSelectedListener true
            } else if (itemId == R.id.nav_transaksi) {
                // Navigate to Transaksi Activity
                startActivity(
                    Intent(
                        this,
                        TransaksiActivity::class.java
                    )
                )
                return@setOnItemSelectedListener true
            } else if (itemId == R.id.nav_laporan) {
                // Navigate to Laporan Activity
                // startActivity(new Intent(this, LaporanActivity.class));
                return@setOnItemSelectedListener true
            } else if (itemId == R.id.nav_profile) {
                // Navigate to Profile Activity
                // startActivity(new Intent(this, ProfileActivity.class));
                return@setOnItemSelectedListener true
            }
            false
        }
    }

    private fun setupTabs() {
        tvPenjualanTab!!.setOnClickListener { v: View? ->
            // Set active tab style
            tvPenjualanTab!!.setBackgroundResource(R.drawable.tab_selected)
            tvPenjualanTab!!.setTextColor(resources.getColor(R.color.text_primary, null))

            tvStokTab!!.background = null
            tvStokTab!!.setTextColor(resources.getColor(R.color.primary_teal, null))

            // Load penjualan data
            loadPenjualanData()
        }

        tvStokTab!!.setOnClickListener { v: View? ->
            // Set active tab style
            tvStokTab!!.setBackgroundResource(R.drawable.tab_selected)
            tvStokTab!!.setTextColor(resources.getColor(R.color.text_primary, null))

            tvPenjualanTab!!.background = null
            tvPenjualanTab!!.setTextColor(resources.getColor(R.color.primary_teal, null))

            // Load stok data
            loadStokData()
        }
    }

    private fun loadDummyData() {
        // Set statistics
        tvTotalTransaksi!!.text = "100"
        tvTotalPendapatan!!.text = "Rp. 500.000"
        tvTotalPengeluaran!!.text = "Rp. 100.000"

        // Load product list
        loadPenjualanData()
    }

    private fun loadPenjualanData() {
        val products: MutableList<Product> = ArrayList()
        products.add(Product("Ayam", "09/10/2025", "5kg"))
        products.add(Product("Sterofoam", "09/10/2025", "1pak"))
        products.add(Product("Bumbu", "09/10/2025", "2pcs"))
        products.add(Product("Timun", "09/10/2025", "1kg"))

        productAdapter!!.setProducts(products)
    }

    private fun loadStokData() {
        val products: MutableList<Product> = ArrayList()
        products.add(Product("Ayam", "09/10/2025", "10kg"))
        products.add(Product("Sterofoam", "09/10/2025", "5pak"))
        products.add(Product("Bumbu", "09/10/2025", "8pcs"))
        products.add(Product("Timun", "09/10/2025", "3kg"))

        productAdapter!!.setProducts(products)
    }
}