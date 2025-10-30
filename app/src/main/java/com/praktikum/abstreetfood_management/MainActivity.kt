//package com.praktikum.abstreetfood_management
//
//import android.os.Bundle
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import androidx.drawerlayout.widget.DrawerLayout
//import androidx.navigation.NavController
//import androidx.navigation.fragment.NavHostFragment
//import androidx.navigation.ui.setupWithNavController
//import com.google.android.material.navigation.NavigationView
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class MainActivity : AppCompatActivity() {
//
//    private lateinit var navController: NavController
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_main)
//
////        val navHostFragment = supportFragmentManager
////            .findFragmentById(R.id.nav_host_container) as NavHostFragment // ✅ Gunakan ID dari FragmentContainerView
////
////        // 2. Dari NavHostFragment tersebut, ambil NavController-nya
////        navController = navHostFragment.navController
////
////
////        // Find the NavController
////        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
////        navController = navHostFragment.navController
//
//        // Setup Drawer Layout
//        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
//        val navView: NavigationView = findViewById(R.id.nav_view)
//
//        // This links the Drawer menu IDs to the destinations in nav_graph.xml
//        navView.setupWithNavController(navController)
//
//        // Listen to destination changes to hide the drawer/bottom nav on initial screens (onboarding/login/register)
//        navController.addOnDestinationChangedListener { _, destination, _ ->
//            if (destination.id == R.id.onboardingFragment || destination.id == R.id.loginFragment || destination.id == R.id.registerFragment) {
//                // Hide or disable bottom nav and drawer for login flow
//                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
//                // You would also need to hide the BottomNavigationView in HomeFragment's *container* or find another way.
//                // Since the BottomNav is *inside* HomeFragment, you'll manage its visibility in HomeFragment.kt
//            } else {
//                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
//            }
//        }
//
//    }
//}

package com.praktikum.abstreetfood_management

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.view.isVisible // Tambahkan import ini jika menggunakan Hilt

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout // Deklarasikan di sini

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Ambil NavHostFragment menggunakan ID yang BENAR
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_container) as NavHostFragment // ✅ Koreksi ID

        // 2. Ambil NavController
        navController = navHostFragment.navController

        // 3. Ambil DrawerLayout dan NavigationView (dengan ID yang sesuai dari Langkah 1)
        drawerLayout = findViewById(R.id.drawerLayout) // ✅ Koreksi ID
        val navView: NavigationView = findViewById(R.id.nav_view)

        // 4. Hubungkan Drawer Menu dengan NavController
        navView.setupWithNavController(navController) // ✅ Menghubungkan item menu drawer ke fragment di nav_graph

        // 5. Atur Logika Visibilitas Drawer (untuk Login/Onboarding)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Daftar ID fragment di mana drawer TIDAK boleh muncul
            val nonDrawerDestinations = setOf(R.id.onboardingFragment, R.id.loginFragment, R.id.registerFragment)

            if (destination.id in nonDrawerDestinations) {
                // Sembunyikan dan kunci drawer pada halaman login/onboarding
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            } else {
                // Buka kunci drawer pada halaman utama
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }
        }
    }
}