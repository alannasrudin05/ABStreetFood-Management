package com.praktikum.abstreetfood_management.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.praktikum.abstreetfood_management.domain.Resource
import com.praktikum.abstreetfood_management.domain.User
import com.praktikum.abstreetfood_management.domain.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * AuthViewModel: Bertanggung jawab atas logika UI untuk Login dan Register.
 * Menggunakan AuthUseCase untuk menjalankan Logika Bisnis.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCase: AuthUseCase // Injeksi Use Case
) : ViewModel() {

    // LiveData untuk memegang status Login (Loading, Success, Error)
    private val _loginState = MutableLiveData<Resource<User>>()
    val loginState: LiveData<Resource<User>> = _loginState

    /**
     * Fungsi untuk memproses Login.
     * Menggunakan Flow dari Use Case untuk menangani status secara reaktif.
     */
    fun processLogin(email: String, password: String) {
        // Validasi dasar
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = Resource.Error("Email dan Kata Sandi wajib diisi.")
            return
        }

        authUseCase.login(email, password).onEach { result ->
            // Mengupdate LiveData setiap kali Flow memancarkan nilai (Loading/Success/Error)
            _loginState.value = result
        }.launchIn(viewModelScope) // Meluncurkan Flow di scope ViewModel
    }

    // Fungsi lain seperti register, reset password, dsb. dapat ditambahkan di sini.

    // Fungsi untuk membersihkan status setelah navigasi
    fun resetLoginState() {
        _loginState.value = null
    }
}
