package com.praktikum.abstreetfood_management.ui

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.praktikum.abstreetfood_management.R
import com.praktikum.abstreetfood_management.databinding.FragmentRegisterBinding

/**
 * A simple [androidx.fragment.app.Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

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
    }
    private fun setupInitialState() {
        updatePasswordUi(isPasswordVisible, binding.tilPassword, binding.etPassword)
        updatePasswordUi(isConfirmPasswordVisible, binding.tilConfirmPassword, binding.etConfirmPassword)
    }

    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener {
            navigateToLogin()
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