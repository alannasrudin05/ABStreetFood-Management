package com.praktikum.abstreetfood_management

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.praktikum.abstreetfood_management.databinding.ActivityMainBinding
//import com.praktikum.abstreetfood_management.utility.SessionManager // Asumsi Anda membuat SessionManager di package ini
import com.praktikum.abstreetfood_management.viewmodel.AuthViewModel // Asumsi Anda membuat AuthViewModel di package ini
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

//    @Inject
//    lateinit var sessionManager: SessionManager // Wajib untuk RBAC

    private val authViewModel: AuthViewModel by viewModels() // Wajib untuk Sign Out

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Ambil NavHostFragment & NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_container) as NavHostFragment
        navController = navHostFragment.navController

        // 2. Hubungkan BottomNavigationView
        binding.bottomNavigation.setupWithNavController(navController)

        // 3. Setup Drawer Menu
        setupDrawerMenu(binding.drawerLayout, binding.navView)

        // 4. Atur Kontrol Visibilitas Navigasi
        setupNavigationVisibilityControl(binding.drawerLayout)

        // 5. Observer Sesi untuk RBAC & Header
        setupSessionObserver(binding.navView)
    }

    /**
     * Mengatur semua logika untuk Drawer Menu (Listener, Sign Out).
     */
    private fun setupDrawerMenu(drawerLayout: DrawerLayout, navView: NavigationView) {
        // Hubungkan item menu drawer ke fragment di nav_graph
        navView.setupWithNavController(navController)

        // GANTI DENGAN LISTENER MANUAL untuk menangani LOGOUT
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_signout -> { // Asumsi ID ini ada di menu drawer Anda
//                    authViewModel.processSignOut()

                    // Navigasi ke Login dan bersihkan stack
                    navController.navigate(R.id.loginFragment, null,
                        androidx.navigation.navOptions {
                            popUpTo(R.id.nav_graph) { // Kembali ke root graph
                                inclusive = true
                            }
                        }
                    )
                    drawerLayout.closeDrawer(GravityCompat.START)
                    return@setNavigationItemSelectedListener true
                }
                else -> {
                    // Untuk semua item menu lainnya, biarkan NavigationUI yang menangani navigasi
                    val handled = NavigationUI.onNavDestinationSelected(menuItem, navController)

                    if (handled) {
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }
                    return@setNavigationItemSelectedListener handled
                }
            }
        }
    }


    /**
     * Mengatur visibilitas Bottom Nav dan Drawer Lock berdasarkan tujuan fragment.
     */
    private fun setupNavigationVisibilityControl(drawerLayout: DrawerLayout) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Daftar ID fragment yang merupakan bagian dari alur autentikasi/onboarding.
            val authDestinations = setOf(
                R.id.onboardingFragment,
                R.id.loginFragment,
                R.id.registerFragment
            )

            // Kontrol Bottom Navigation
            if (destination.id in authDestinations) {
                // Sembunyikan Bottom Nav dan kunci Drawer di halaman Auth
                binding.bottomNavigation.visibility = View.GONE
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            } else {
                // Tampilkan Bottom Nav dan buka kunci Drawer di halaman utama
                binding.bottomNavigation.visibility = View.VISIBLE
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }

            // Opsional: Sembunyikan Bottom Nav juga saat berada di detail/transaksi penuh
            if (destination.id == R.id.transaksiFragment) {
                binding.bottomNavigation.visibility = View.GONE
            }

            if (destination.id == R.id.notaFragment) {
                binding.bottomNavigation.visibility = View.GONE
            }
        }
    }


    /**
     * Mengamati Role di SessionManager (StateFlow) dan memperbarui menu/header/RBAC.
     */
    private fun setupSessionObserver(navView: NavigationView) {
        lifecycleScope.launch {
            // collectLatest akan memproses nilai terbaru dari Flow role pengguna
//            sessionManager.userRoleFlow.collectLatest {
//                applyRoleRestrictions(navView) // Terapkan pembatasan RBAC
//                updateDrawerHeader(navView)   // Perbarui header (Nama & Role)
//                // checkUserAccess()           // Opsional: Tambahkan guard navigasi
//            }
        }
    }


    /**
     * Mengisi Nama dan ROLE di Header Drawer.
     */
    private fun updateDrawerHeader(navView: NavigationView) {
        // Asumsi header view layout memiliki ID root 0
        val headerView = navView.getHeaderView(0)

        // Asumsi ID TextViews di drawer header adalah ini
//        val nameTextView = headerView.findViewById<TextView>(R.id.textViewUserName)
//        val roleTextView = headerView.findViewById<TextView>(R.id.textViewUserRole)

//        val name = sessionManager.getUserName() ?: "Pengguna"
//        val role = sessionManager.getUserRole() ?: "tamu"

//        nameTextView.text = name
//
//        // Kapitalisasi Role untuk tampilan yang rapi
//        roleTextView.text = role.replaceFirstChar {
//            if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
//        }
    }

    /**
     * LOGIKA RBAC UTAMA: Menyembunyikan item-item menu di drawer berdasarkan peran pengguna.
     * Sesuaikan ID menu dan logika peran Anda ('admin', 'owner', 'cashier').
     */
//    private fun applyRoleRestrictions(navView: NavigationView) {
//        val menu = navView.menu
//        val userRole = sessionManager.getUserRole()
//
//        // Daftar ID menu untuk setiap peran (Ganti dengan ID Fragment/Menu ABStreetFood Anda)
//        val adminItems = listOf(R.id.adminDashboardFragment, R.id.pengaturanFragment)
//        val ownerItems = listOf(R.id.analisisFragment)
//        val cashierItems = listOf(R.id.homeFragment, R.id.transaksiFragment, R.id.inventarisFragment)
//        val sharedItems = listOf(R.id.action_signout) // Selalu terlihat jika sudah login
//
//        // Gabungkan semua item unik yang mungkin ada di drawer
//        val allDrawerItems = (adminItems + ownerItems + cashierItems + sharedItems).toSet()
//
//        for (itemId in allDrawerItems) {
//            val item = menu.findItem(itemId)
//            if (item != null) {
//                item.isVisible = when (itemId) {
//                    // Item Kasir (dilihat oleh semua peran kecuali Owner jika ada batasan)
//                    in cashierItems -> userRole == "cashier" || userRole == "admin" || userRole == "owner"
//
//                    // Item Owner
//                    in ownerItems -> userRole == "owner" || userRole == "admin"
//
//                    // Item Admin
//                    in adminItems -> userRole == "admin"
//
//                    // Item Shared (Signout)
//                    in sharedItems -> sessionManager.isLoggedIn() // Asumsi Anda punya fungsi ini
//
//                    else -> false
//                }
//            }
//        }
//    }
}



////package com.praktikum.abstreetfood_management
////
////import android.os.Bundle
////import androidx.activity.enableEdgeToEdge
////import androidx.appcompat.app.AppCompatActivity
////import androidx.core.view.ViewCompat
////import androidx.core.view.WindowInsetsCompat
////import androidx.drawerlayout.widget.DrawerLayout
////import androidx.navigation.NavController
////import androidx.navigation.fragment.NavHostFragment
////import androidx.navigation.ui.setupWithNavController
////import com.google.android.material.navigation.NavigationView
////import dagger.hilt.android.AndroidEntryPoint
////
////@AndroidEntryPoint
////class MainActivity : AppCompatActivity() {
////
////    private lateinit var navController: NavController
////
////    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////        enableEdgeToEdge()
////        setContentView(R.layout.activity_main)
////
//////        val navHostFragment = supportFragmentManager
//////            .findFragmentById(R.id.nav_host_container) as NavHostFragment // ✅ Gunakan ID dari FragmentContainerView
//////
//////        // 2. Dari NavHostFragment tersebut, ambil NavController-nya
//////        navController = navHostFragment.navController
//////
//////
//////        // Find the NavController
//////        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//////        navController = navHostFragment.navController
////
////        // Setup Drawer Layout
////        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
////        val navView: NavigationView = findViewById(R.id.nav_view)
////
////        // This links the Drawer menu IDs to the destinations in nav_graph.xml
////        navView.setupWithNavController(navController)
////
////        // Listen to destination changes to hide the drawer/bottom nav on initial screens (onboarding/login/register)
////        navController.addOnDestinationChangedListener { _, destination, _ ->
////            if (destination.id == R.id.onboardingFragment || destination.id == R.id.loginFragment || destination.id == R.id.registerFragment) {
////                // Hide or disable bottom nav and drawer for login flow
////                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
////                // You would also need to hide the BottomNavigationView in HomeFragment's *container* or find another way.
////                // Since the BottomNav is *inside* HomeFragment, you'll manage its visibility in HomeFragment.kt
////            } else {
////                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
////            }
////        }
////
////    }
////}
//
//package com.praktikum.abstreetfood_management
//
//import android.os.Bundle
//import android.view.View
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import androidx.drawerlayout.widget.DrawerLayout
//import androidx.navigation.NavController
//import androidx.navigation.fragment.NavHostFragment
//import androidx.navigation.ui.setupWithNavController
//import com.google.android.material.navigation.NavigationView
//import dagger.hilt.android.AndroidEntryPoint
//import androidx.core.view.isVisible // Tambahkan import ini jika menggunakan Hilt
//import com.praktikum.abstreetfood_management.databinding.ActivityMainBinding
//
//@AndroidEntryPoint
//class MainActivity : AppCompatActivity() {
//
//    private lateinit var navController: NavController
////    private lateinit var drawerLayout: DrawerLayout // Deklarasikan di sini
//    private lateinit var binding: ActivityMainBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        // 1. Ambil NavHostFragment menggunakan ID yang BENAR
//        val navHostFragment = supportFragmentManager
//            .findFragmentById(R.id.nav_host_container) as NavHostFragment // ✅ Koreksi ID
//        navController = navHostFragment.navController
//
//        val drawerLayout: DrawerLayout? = findViewById(R.id.drawerLayout)
////        drawerLayout = findViewById(R.id.drawerLayout)
//        val navView: NavigationView? = findViewById(R.id.nav_view)
//
//        // 1. Setup Bottom Nav di sini (Pola Centralized Control)
//        binding.bottomNavigation.setupWithNavController(navController)
//
//        if (drawerLayout != null && navView != null) {
//            binding.navView.setupWithNavController(navController)
//            setupSessionObserver(navView)
//
//
//            // 2. Kontrol Visibilitas Bottom Nav
//            navController.addOnDestinationChangedListener { _, destination, _ ->
//                when (destination.id) {
//                    R.id.onboardingFragment, R.id.loginFragment, R.id.registerFragment, R.id.transaksiFragment -> {
//                        // Sembunyikan Bottom Nav pada layar sebelum login
//                        binding.bottomNavigation.visibility = View.GONE
//                    }
//                    else -> {
//                        // Tampilkan Bottom Nav pada HomeFragment dan layar lainnya
//                        binding.bottomNavigation.visibility = View.VISIBLE
//                    }
//                }
//            }
//
//
//            binding.navView.setupWithNavController(navController)
//            // 4. Hubungkan Drawer Menu dengan NavController
//    //        navView.setupWithNavController(navController) // ✅ Menghubungkan item menu drawer ke fragment di nav_graph
//
//            // 5. Atur Logika Visibilitas Drawer (untuk Login/Onboarding)
//            navController.addOnDestinationChangedListener { _, destination, _ ->
//                // Daftar ID fragment di mana drawer TIDAK boleh muncul
//                val nonDrawerDestinations = setOf(R.id.onboardingFragment, R.id.loginFragment, R.id.registerFragment)
//
//                if (destination.id in nonDrawerDestinations) {
//                    // Sembunyikan dan kunci drawer pada halaman login/onboarding
//                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
//                } else {
//                    // Buka kunci drawer pada halaman utama
//                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
//                }
//            }
//
//        }
//    }
//}