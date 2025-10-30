package com.praktikum.abstreetfood_management.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.praktikum.abstreetfood_management.MainActivity
import com.praktikum.abstreetfood_management.R
import com.praktikum.abstreetfood_management.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val parentNavController = findNavController()

        // Setup Bottom Navigation
        val bottomNavView = binding.bottomNavigation // Menggunakan binding karena Anda sudah setup ViewBinding
        // This links the BottomNavigationView item IDs (from bottom_nav_menu.xml)
        // to the destinations in nav_graph.xml using the parentNavController.
        // Note: nav_home, nav_transaksi, nav_laporan, nav_profile IDs must match
        // the destination IDs in nav_graph.xml (homeFragment, transaksiFragment, etc.)
        bottomNavView.setupWithNavController(parentNavController)

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
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}