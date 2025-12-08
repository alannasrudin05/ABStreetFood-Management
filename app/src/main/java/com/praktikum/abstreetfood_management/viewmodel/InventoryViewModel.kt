package com.praktikum.abstreetfood_management.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.praktikum.abstreetfood_management.data.repository.IProductRepository
import com.praktikum.abstreetfood_management.data.repository.ITransactionRepository
import com.praktikum.abstreetfood_management.domain.model.NewTransaction
import com.praktikum.abstreetfood_management.domain.model.ProductItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.lifecycle.asLiveData
import com.praktikum.abstreetfood_management.domain.usecase.SessionUseCase

@HiltViewModel
class InventoryViewModel @Inject constructor(
//    private val inventoryRepository: IInventoryRepository,
    private val productRepository: IProductRepository,
    private val sessionUseCase: SessionUseCase,
) : ViewModel() {

    // LiveData untuk memantau status penyimpanan
    private val _saveTransactionStatus = MutableLiveData<Result<Unit>>()
    val saveTransactionStatus: LiveData<Result<Unit>> = _saveTransactionStatus

    val productItems: LiveData<List<ProductItem>> =
        productRepository.getAllProductItems().asLiveData()

    private val _currentUserId = MutableLiveData<String?>()
    val currentUserId: LiveData<String?> = _currentUserId
//
//    private val sharedPrefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
//
//    val userId = sharedPrefs.getString("user_id", null)


    init {
        // Ambil ID pengguna segera setelah ViewModel dibuat
        viewModelScope.launch {
            sessionUseCase.getCurrentUserId().collect { id ->
                _currentUserId.value = id
            }
        }
    }
//    fun recordTransaction(newTransaction: NewTransaction) {
//        viewModelScope.launch {
//            val result = transactionRepository.recordNewTransaction(newTransaction)
//            _saveTransactionStatus.postValue(result)
//        }
//    }

    // TODO: Tambahkan fungsi untuk memuat daftar ProductItem (untuk top products)
}