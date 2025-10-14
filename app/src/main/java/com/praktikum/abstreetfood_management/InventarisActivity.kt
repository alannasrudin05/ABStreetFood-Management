package com.praktikum.abstreetfood_management

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class InventarisActivity : AppCompatActivity() {

    private lateinit var rvBahan: RecyclerView
    private lateinit var rvNotifikasi: RecyclerView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var bahanAdapter: BahanAdapter
    private lateinit var notifikasiAdapter: NotifikasiAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventaris)

        initViews()
        setupRecyclerViews()
        setupBottomNavigation()
        setupFabButton()
        loadDummyData()
    }

    private fun initViews() {
        rvBahan = findViewById(R.id.rvBahan)
        rvNotifikasi = findViewById(R.id.rvNotifikasi)
        fabAdd = findViewById(R.id.fabAdd)
        bottomNavigation = findViewById(R.id.bottomNavigation)
    }

    private fun setupRecyclerViews() {
        // Setup Bahan RecyclerView
        rvBahan.layoutManager = LinearLayoutManager(this)
        bahanAdapter = BahanAdapter(this)
        rvBahan.adapter = bahanAdapter

        // Setup edit click listener
        bahanAdapter.setOnItemClickListener(object : BahanAdapter.OnItemClickListener {
            override fun onEditClick(bahan: Bahan, position: Int) {
                // Handle edit click - implementasi nanti
                Toast.makeText(this@InventarisActivity, "Edit ${bahan.name}", Toast.LENGTH_SHORT).show()
            }
        })

        // Setup Notifikasi RecyclerView
        rvNotifikasi.layoutManager = LinearLayoutManager(this)
        notifikasiAdapter = NotifikasiAdapter(this)
        rvNotifikasi.adapter = notifikasiAdapter
    }

    private fun setupBottomNavigation() {
        bottomNavigation.selectedItemId = R.id.nav_home // Set ke home karena belum ada menu inventaris
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    finish() // Back to home
                    true
                }
                R.id.nav_transaksi -> {
                    // Navigate to Transaksi Activity
                    startActivity(Intent(this, TransaksiActivity::class.java))
                    true
                }
                R.id.nav_laporan -> {
                    // Navigate to Laporan Activity
                    // startActivity(Intent(this, LaporanActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    // Navigate to Profile Activity
                    // startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun setupFabButton() {
        fabAdd.setOnClickListener {
            // Handle add new bahan - implementasi nanti
            Toast.makeText(this, "Tambah Bahan Baku", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadDummyData() {
        // Load dummy data untuk bahan
        val bahanList = ArrayList<Bahan>()
        bahanList.add(Bahan("Ayam", "5 kg", true)) // Low stock
        bahanList.add(Bahan("Nasi", "10 kg", false)) // Normal stock

        bahanAdapter.setBahanList(bahanList)

        // Load dummy data untuk notifikasi
        val notifikasiList = ArrayList<Notifikasi>()
        notifikasiList.add(Notifikasi("Ayam", "Stok tersisa 5 kg (minimum: 10kg)"))

        notifikasiAdapter.setNotifikasiList(notifikasiList)
    }
}