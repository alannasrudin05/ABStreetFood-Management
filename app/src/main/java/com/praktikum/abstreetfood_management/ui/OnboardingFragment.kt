package com.praktikum.abstreetfood_management.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.launch
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.praktikum.abstreetfood_management.R
import com.praktikum.abstreetfood_management.databinding.FragmentOnboardingBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * A simple [androidx.fragment.app.Fragment] subclass.
 * Use the [OnboardingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        Handler(Looper.getMainLooper()).postDelayed({
//            navigateToLogin()
//        }, 3000L)
        navController = findNavController()

        viewLifecycleOwner.lifecycleScope.launch {
            // Tahan selama 3 detik (3000 milidetik)
            delay(3000L)

            // Panggil navigasi setelah penundaan selesai
            navigateToLogin()
        }

//        setupClickListeners()
    }

    private fun setupClickListeners() {
//        binding.btnLogin.setOnClickListener {
//            navigateToLogin()
//        }

    }

    private fun navigateToLogin() {
        val action = R.id.action_onboardingFragment_to_loginFragment
        navController.navigate(action)


    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}