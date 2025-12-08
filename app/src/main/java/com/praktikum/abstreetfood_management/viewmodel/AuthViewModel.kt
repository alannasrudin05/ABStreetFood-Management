package com.praktikum.abstreetfood_management.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.praktikum.abstreetfood_management.utility.Resource
import com.praktikum.abstreetfood_management.domain.model.User
import com.praktikum.abstreetfood_management.domain.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
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
    private val _loginState = MutableLiveData<Resource<User>?>()
    val loginState: LiveData<Resource<User>?> = _loginState

    private val _registerState = MutableLiveData<Resource<User>?>()
    val registerState: LiveData<Resource<User>?> = _registerState

    private val _currentUserId = MutableLiveData<String?>()
    val currentUserId: LiveData<String?> = _currentUserId
    /**
     * Fungsi untuk memproses Login.
     * Menggunakan Flow dari Use Case untuk menangani status secara reaktif.
     */
    fun processLogin(email: String, password: String) {
        Timber.d("AUTH_VIEWMODEL: Menerima panggilan login. Email: $email") // <<< LOG BARU
        // Validasi dasar
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = Resource.Error("Email dan Kata Sandi wajib diisi.")
            return
        }

        viewModelScope.launch {
            authUseCase.login(email, password)
                .collectLatest { resource ->
                    // LOG D: Memastikan Flow dari UseCase mengalir ke ViewModel
                    Timber.d("AUTH_VIEWMODEL: Menerima state: ${resource.javaClass.simpleName}") // <<< LOG BARU
                    _loginState.value = resource
                }
        }
    }
    /**
     * Fungsi untuk memproses Register. (Pelengkap yang diminta)
     */
    fun processRegister(name: String, email: String, password: String) {
        // Reset state sebelumnya
        _registerState.value = null

        // Validasi dasar
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _registerState.value = Resource.Error("Semua kolom wajib diisi.")
            return
        }

        authUseCase.register(name, email, password).onEach { result ->
            Log.d("REGISTER_FLOW", "8. ViewModel received state: ${result.javaClass.simpleName}")
            _registerState.value = result
        }.launchIn(viewModelScope)
    }
    fun getUserIdSync(): String? {
        return _currentUserId.value
    }

    /**
     * Fungsi untuk membersihkan status setelah navigasi atau proses selesai.
     */
    fun resetLoginState() {
        _loginState.value = null
        Timber.d("AUTH_VIEWMODEL: Login state direset.")
    }

    fun resetRegisterState() {
        _registerState.value = null
    }
}
