package com.praktikum.abstreetfood_management.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.praktikum.abstreetfood_management.R
import com.praktikum.abstreetfood_management.data.local.preferences.SyncPreferenceManager
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PengaturanFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PengaturanFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var syncPrefsManager: SyncPreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pengaturan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        syncPrefsManager = SyncPreferenceManager(requireContext())

        // 1. Tampilkan status awal
        viewLifecycleOwner.lifecycleScope.launch {
            syncPrefsManager.isSyncEnabledFlow.collect { isEnabled ->
                // binding.syncToggle.isChecked = isEnabled // Asumsi ada elemen syncToggle
            }
        }

        // 2. Handle perubahan toggle
        // binding.syncToggle.setOnCheckedChangeListener { _, isChecked ->
        //     viewLifecycleOwner.lifecycleScope.launch {
        //         syncPrefsManager.setSyncEnabled(isChecked)
        //         // Opsi: Jika dinonaktifkan, batalkan WorkManager. Jika diaktifkan, mulai ulang.
        //     }
        // }
    }
}