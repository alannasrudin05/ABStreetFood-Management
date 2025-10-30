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
// yaitu fragment_edit_profile.xml menjadi FragmentEditProfileBinding
import com.praktikum.abstreetfood_management.databinding.FragmentEditProfileBinding

class EditProfileFragment : Fragment() {

    // Deklarasi properti binding
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout menggunakan ViewBinding
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mengatur teks judul
        binding.tvTitle.text = getString(R.string.edit_profile_title)

        // --- Data Dummy (untuk mengisi form saat dibuka) ---
        binding.etNama.setText("Nama Pengguna")
        binding.etUsername.setText("usernameku")
        binding.tvGenderSelected.text = "Pria" // Ganti dengan data aktual
        binding.etEmail.setText("user@example.com")

        // --- Event Listener ---

        // 1. Tombol Kembali
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack() // Kembali ke fragment sebelumnya (ProfileFragment)
        }

        // 2. Tombol Simpan Perubahan
        binding.btnSimpan.setOnClickListener {
            saveProfileChanges()
        }

        // 3. Memilih Jenis Kelamin
        binding.layoutGender.setOnClickListener {
            // TODO: Implementasi logika untuk menampilkan Dialog/BottomSheet untuk memilih gender
            Toast.makeText(context, "Membuka pilihan Jenis Kelamin...", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Fungsi untuk memvalidasi dan menyimpan perubahan profil.
     */
    private fun saveProfileChanges() {
        val nama = binding.etNama.text.toString().trim()
        val username = binding.etUsername.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val gender = binding.tvGenderSelected.text.toString().trim()

        // Validasi Sederhana
        if (nama.isEmpty()) {
            Toast.makeText(context, "Nama tidak boleh kosong.", Toast.LENGTH_SHORT).show()
            return
        }
        if (username.isEmpty()) {
            Toast.makeText(context, "Username tidak boleh kosong.", Toast.LENGTH_SHORT).show()
            return
        }
        if (email.isEmpty()) {
            Toast.makeText(context, "Email tidak boleh kosong.", Toast.LENGTH_SHORT).show()
            return
        }
        // TODO: Tambahkan validasi format email

        // TODO: Panggil fungsi untuk menyimpan data (ke ViewModel/Repository/Server)

        // Setelah sukses menyimpan
        Toast.makeText(context, "Perubahan profil berhasil disimpan!", Toast.LENGTH_SHORT).show()

        // Kembali ke Profile Fragment
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Penting: Kosongkan binding saat view dihancurkan.
        _binding = null
    }

    // Companion object dipertahankan jika Anda menggunakannya
    // (Anda bisa menghapus bagian ini jika tidak digunakan lagi)
}