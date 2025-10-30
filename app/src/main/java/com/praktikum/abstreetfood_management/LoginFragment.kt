package com.praktikum.abstreetfood_management

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.praktikum.abstreetfood_management.databinding.FragmentLoginBinding // Ganti jika nama package berbeda
import com.praktikum.abstreetfood_management.domain.Resource
import com.praktikum.abstreetfood_management.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.fragment.app.viewModels
import javax.inject.Inject

/**
 * LoginFragment: Bertanggung jawab HANYA untuk menampilkan UI dan
 * meneruskan event (klik tombol) ke NavController.
 */
@AndroidEntryPoint
class LoginFragment : Fragment() {

    // 1. Deklarasi View Binding
    // Dibuat nullable dan di-reset di onDestroyView untuk menghindari memory leak.
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // Injeksi ViewModel
    private val authViewModel: AuthViewModel by viewModels()

//    @Inject
//    lateinit var authViewModel: AuthViewModel

    // 2. Deklarasi NavController (Prinsip Clean Architecture)
    // NavController sebaiknya diinisialisasi sekali saja.
    private lateinit var navController: NavController
    private var isPasswordVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 3. Inflate layout menggunakan View Binding
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root // Kembalikan root view dari binding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 4. Inisialisasi NavController
        navController = findNavController()
        setupInitialState()
        // 5. Panggil fungsi untuk setup listener (Prinsip Clean Architecture)
        setupClickListeners()
        observeLoginState()
    }
    private fun setupInitialState() {
        updatePasswordUi(isPasswordVisible, binding.tilPassword, binding.etPassword)
    }

    private fun setupClickListeners() {
        // 6. Menggunakan binding untuk mengakses View (lebih aman dan efisien)
        binding.tvRegister.setOnClickListener {
            // Logika navigasi dipisahkan ke fungsi sendiri
            navigateToRegister()
        }
        // Mengatur listener untuk ikon mata pada field Password
        binding.tilPassword.setEndIconOnClickListener {
            // Balik state spesifik untuk field ini
            isPasswordVisible = !isPasswordVisible
            // Panggil fungsi UI update yang generik
            updatePasswordUi(isPasswordVisible, binding.tilPassword, binding.etPassword)
        }
//
        binding.btnLogin.setOnClickListener {
            // (Asumsi: btnHome adalah ID untuk tombol login)
            // Logika navigasi dipisahkan ke fungsi sendiri
//            navigateToHome()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            authViewModel.processLogin(email, password)
        }
    }

    /**
     * Mengamati LiveData dari ViewModel untuk bereaksi terhadap perubahan state.
     */
    private fun observeLoginState() {
        authViewModel.loginState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Tampilkan loading spinner atau disable tombol
                    binding.btnLogin.isEnabled = false
                    // Tampilkan progress bar atau pesan loading (implementasikan sendiri)
                    Toast.makeText(requireContext(), "Sedang Memuat...", Toast.LENGTH_SHORT).show()
                }
                is Resource.Success -> {
                    // Login berhasil! Tampilkan pesan sukses dan navigasi.
                    Toast.makeText(requireContext(), "Selamat datang, ${resource.data?.name}!", Toast.LENGTH_LONG).show()
                    navigateToHome()
                    authViewModel.resetLoginState() // Reset status setelah navigasi
                }
                is Resource.Error -> {
                    // Login gagal atau error jaringan
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(requireContext(), "Gagal: ${resource.message}", Toast.LENGTH_LONG).show()
                    // Opsional: Tampilkan data jika ada (kasus Local Fallback)
                    if (resource.data != null) {
                        Toast.makeText(requireContext(), "Menggunakan data offline untuk sementara.", Toast.LENGTH_LONG).show()
                        navigateToHome()
                        authViewModel.resetLoginState()
                    }
                }
                null -> {
                    // Status reset, enable tombol lagi
                    binding.btnLogin.isEnabled = true
                }
            }
        }
    }

    // 7. Fungsi navigasi sesuai prinsip Clean Architecture (Single Responsibility)
    private fun navigateToRegister() {
        // Fungsi ini hanya bertanggung jawab untuk navigasi ke Register
        val action = R.id.action_loginFragment_to_registerFragment
        navController.navigate(action)
    }

    private fun navigateToHome() {
        // Fungsi ini hanya bertanggung jawab untuk navigasi ke Home
        val action = R.id.action_loginFragment_to_homeFragment
        navController.navigate(action)
    }

    /**
     * TANGGUNG JAWAB #4: Memperbarui UI berdasarkan state (BISA DIGUNAKAN KEMBALI).
     * Fungsi ini tidak peduli field mana yang diubah, ia hanya menerapkan state ke View yang diberikan.
     * @param isVisible Status visibilitas saat ini.
     * @param textInputLayout Layout yang ikonnya akan diubah.
     * @param textInputEditText Field yang tipe inputnya akan diubah.
     */
    private fun updatePasswordUi(
        isVisible: Boolean,
        textInputLayout: TextInputLayout,
        textInputEditText: TextInputEditText
    ) {
        // Tentukan ikon dan tipe input berdasarkan state 'isVisible'
        val (iconRes, inputType) = if (isVisible) {
            // JIKA HARUS TERLIHAT:
            // Ikon mata tercoret, input tipe teks terlihat.
            Pair(R.drawable.eye, InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
        } else {
            // JIKA HARUS TERSEMBUNYI:
            // Ikon mata terbuka, input tipe password.
            Pair(R.drawable.eye_closed, InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
        }

        // Terapkan perubahan ke UI
        textInputLayout.setEndIconDrawable(iconRes)
        textInputEditText.inputType = inputType

        // Pindahkan kursor ke akhir teks setelah mengubah tipe input.
        textInputEditText.setSelection(textInputEditText.text?.length ?: 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 8. Bersihkan binding untuk menghindari memory leak
        _binding = null
    }
}
