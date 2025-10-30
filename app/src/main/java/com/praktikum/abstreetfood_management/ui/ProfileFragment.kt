package com.praktikum.abstreetfood_management.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.praktikum.abstreetfood_management.R
// Pastikan nama paket Binding sesuai dengan nama file layout Anda,
// yaitu fragment_profile.xml menjadi FragmentProfileBinding
import com.praktikum.abstreetfood_management.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    // Deklarasi properti binding
    private var _binding: FragmentProfileBinding? = null
    // Properti ini hanya valid antara onCreateView dan onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout menggunakan ViewBinding
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mengatur teks judul
        binding.tvTitle.text = getString(R.string.profile)

        // --- Event Listener ---

        // 1. Pindah ke Edit Profile Fragment
        binding.cardEditProfile.setOnClickListener {
            // Menggunakan ID Action yang telah didefinisikan di nav_graph.xml
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        // 2. Tombol Kembali (untuk skenario jika Fragment ini bisa di-back)
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // 3. Tombol Sign Out
        binding.btnSignOut.setOnClickListener {
            // TODO: Tampilkan dialog konfirmasi sebelum Sign Out
            Toast.makeText(requireContext(), "Sign Out diklik", Toast.LENGTH_SHORT).show()
            // Contoh aksi: findNavController().navigate(R.id.action_global_to_loginFragment)
        }

        // 4. Ubah Password
        binding.cardChangePassword.setOnClickListener {
            // TODO: Pindah ke Fragment Ubah Password
            Toast.makeText(requireContext(), "Ubah Password diklik", Toast.LENGTH_SHORT).show()
        }

        // --- Data Dummy ---
        // Anda bisa mengganti ini dengan data dari ViewModel atau SharedPreferences
        binding.tvUserName.text = "AB Street Food Manager"
        binding.tvUserRole.text = getString(R.string.karyawan) // Ambil dari strings.xml
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Penting: Kosongkan binding saat view dihancurkan untuk menghindari kebocoran memori.
        _binding = null
    }

    // Companion object dipertahankan jika Anda menggunakannya
    // (Anda bisa menghapus bagian ini jika tidak digunakan lagi)
}