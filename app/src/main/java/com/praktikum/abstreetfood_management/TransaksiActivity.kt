package com.praktikum.abstreetfood_management

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class TransaksiActivity : AppCompatActivity() {

    private lateinit var ivBack: ImageView
    private lateinit var btnAyamGoreng: Button
    private lateinit var btnAyamBakar: Button
    private lateinit var btnSimpan: Button
    private lateinit var etJumlah: EditText
    private lateinit var etHarga: EditText
    private lateinit var rvRiwayat: RecyclerView
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var riwayatAdapter: RiwayatAdapter

    private var selectedMenu: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaksi)

        initViews()
        setupRecyclerView()
        setupBottomNavigation()
        setupMenuButtons()
        setupSimpanButton()
        loadDummyRiwayat()
    }

    private fun initViews() {
        ivBack = findViewById(R.id.ivBack)
//        btnAyamGoreng = findViewById(R.id.btnAyamGoreng)
//        btnAyamBakar = findViewById(R.id.btnAyamBakar)
//        btnSimpan = findViewById(R.id.btnSimpan)
//        etJumlah = findViewById(R.id.etJumlah)
//        etHarga = findViewById(R.id.etHarga)
        rvRiwayat = findViewById(R.id.rvRiwayat)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        ivBack.setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        rvRiwayat.layoutManager = LinearLayoutManager(this)
        riwayatAdapter = RiwayatAdapter(this)
        rvRiwayat.adapter = riwayatAdapter
    }

    private fun setupBottomNavigation() {
        bottomNavigation.selectedItemId = R.id.nav_transaksi
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    finish() // Back to home
                    true
                }
                R.id.nav_transaksi -> {
                    // Already on transaksi
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

    private fun setupMenuButtons() {
        btnAyamGoreng.setOnClickListener {
            selectedMenu = "Ayam Goreng"
            btnAyamGoreng.setBackgroundResource(R.drawable.tab_selected)
            btnAyamGoreng.setTextColor(ContextCompat.getColor(this, R.color.text_primary))

            btnAyamBakar.setBackgroundResource(R.drawable.button_menu_unselected)
            btnAyamBakar.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
        }

        btnAyamBakar.setOnClickListener {
            selectedMenu = "Ayam Bakar"
            btnAyamBakar.setBackgroundResource(R.drawable.tab_selected)
            btnAyamBakar.setTextColor(ContextCompat.getColor(this, R.color.text_primary))

            btnAyamGoreng.setBackgroundResource(R.drawable.button_menu_unselected)
            btnAyamGoreng.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
        }
    }

    private fun setupSimpanButton() {
        btnSimpan.setOnClickListener {
            val jumlah = etJumlah.text.toString()
            val harga = etHarga.text.toString()

            if (selectedMenu.isEmpty() || jumlah.isEmpty() || harga.isEmpty()) {
                // Show error - implementasi nanti
                return@setOnClickListener
            }

            // Save transaction logic here
            // For now, just clear the fields
            etJumlah.setText("")
            etHarga.setText("")
            selectedMenu = ""
            btnAyamGoreng.setBackgroundResource(R.drawable.button_menu_unselected)
            btnAyamBakar.setBackgroundResource(R.drawable.button_menu_unselected)
        }
    }

    private fun loadDummyRiwayat() {
        val riwayatList = ArrayList<Riwayat>()
        riwayatList.add(Riwayat("Ayam Goreng", "Nasi", "Jumlah 2x", "Rp.20.000", "15.00"))
        riwayatList.add(Riwayat("Ayam Bakar", "Nasi", "Jumlah 2x", "Rp.20.000", "15.00"))

        riwayatAdapter.setRiwayatList(riwayatList)
    }
}