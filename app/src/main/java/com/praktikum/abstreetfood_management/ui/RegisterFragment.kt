package com.praktikum.abstreetfood_management.ui

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.praktikum.abstreetfood_management.R
import com.praktikum.abstreetfood_management.databinding.FragmentRegisterBinding
import com.praktikum.abstreetfood_management.viewmodel.AuthViewModel
import androidx.fragment.app.viewModels
import com.praktikum.abstreetfood_management.utility.Resource
import dagger.hilt.android.AndroidEntryPoint

/**
 * A simple [androidx.fragment.app.Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()
        setupInitialState()
        setupClickListeners()
        setupObservers()
    }

    private fun setupObservers() {
        authViewModel.registerState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> { /* Tampilkan loading spinner */ }
                is Resource.Success -> {
                    Log.i("REGISTER_FLOW", "9. Fragment received SUCCESS. Navigating.")
                    Toast.makeText(context, "Registrasi Berhasil! Silakan Login.", Toast.LENGTH_LONG).show()
                    navigateToLogin()
                }
                is Resource.Error -> {
                    Log.e("REGISTER_FLOW", "10. Fragment received ERROR: ${resource.message}")
                    Toast.makeText(context, "Gagal Registrasi: ${resource.message}", Toast.LENGTH_LONG).show()
                    // Hentikan loading spinner
                }
                null -> {}
            }
        }
    }

    private fun setupInitialState() {
        updatePasswordUi(isPasswordVisible, binding.tilPassword, binding.etPassword)
        updatePasswordUi(isConfirmPasswordVisible, binding.tilConfirmPassword, binding.etConfirmPassword)
    }

    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener {
//            navigateToLogin()
            attemptRegister()
            Log.e("REGISTER_FLOW", "Tombol di klik")
        }

        binding.tvLogin.setOnClickListener {
            navigateToLogin()
        }

        binding.tilPassword.setEndIconOnClickListener {
            isPasswordVisible = !isPasswordVisible
            updatePasswordUi(isPasswordVisible, binding.tilPassword, binding.etPassword)
        }

        binding.tilConfirmPassword.setEndIconOnClickListener {
            isConfirmPasswordVisible = !isConfirmPasswordVisible
            updatePasswordUi(isConfirmPasswordVisible, binding.tilConfirmPassword, binding.etConfirmPassword)
        }
    }

    private fun attemptRegister() {
        // Asumsi Anda telah menambahkan etName ke fragment_register.xml
        val name = binding.etName.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(context, "Semua field wajib diisi.", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(context, "Password dan Konfirmasi harus sama.", Toast.LENGTH_SHORT).show()
            return
        }

        // Panggil Use Case untuk Register
//        authViewModel.registerUser(name, email, password)
        authViewModel.processRegister(name, email, password)
    }

    private fun navigateToLogin() {
        val action = R.id.action_registerFragment_to_loginFragment
        navController.navigate(action)
    }

    private fun updatePasswordUi(
        isVisible: Boolean,
        textInputLayout: TextInputLayout,
        textInputEditText: TextInputEditText
    ) {
        val (iconRes, inputType) = if (isVisible) {
            Pair(R.drawable.eye, InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
        } else {
            Pair(R.drawable.eye_closed, InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
        }

        textInputLayout.setEndIconDrawable(iconRes)
        textInputEditText.inputType = inputType

        textInputEditText.setSelection(textInputEditText.text?.length ?: 0)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}