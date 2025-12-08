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
import androidx.lifecycle.liveData
import com.praktikum.abstreetfood_management.data.repository.DailySalesData
import com.praktikum.abstreetfood_management.domain.model.Transaction
import com.praktikum.abstreetfood_management.domain.usecase.SessionUseCase

@HiltViewModel
class TransaksiViewModel @Inject constructor(
    private val transactionRepository: ITransactionRepository,
    private val productRepository: IProductRepository,
    private val sessionUseCase: SessionUseCase,
) : ViewModel() {

    // LiveData untuk memantau status penyimpanan
//    private val _saveTransactionStatus = MutableLiveData<Result<Unit>>()
//    val saveTransactionStatus: LiveData<Result<Unit>> = _saveTransactionStatus
 private val _saveTransactionStatus = MutableLiveData<Result<String>>()
    val saveTransactionStatus: LiveData<Result<String>> = _saveTransactionStatus

    val productItems: LiveData<List<ProductItem>> =
        productRepository.getAllProductItems().asLiveData()

    private val _currentUserId = MutableLiveData<String?>()
    val currentUserId: LiveData<String?> = _currentUserId

    private val _currentUserName = MutableLiveData<String?>()
    val currentUserName: LiveData<String?> = _currentUserName // ✅ LiveData yang diekspos
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
        viewModelScope.launch {
            // Asumsi SessionUseCase memiliki fungsi getCurrentUserName()
            sessionUseCase.getCurrentUserName().collect { name ->
                _currentUserName.value = name
            }
        }
    }
    fun recordTransaction(newTransaction: NewTransaction) {
        viewModelScope.launch {
            val result = transactionRepository.recordNewTransaction(newTransaction)
            _saveTransactionStatus.postValue(result)
        }
    }

    val transactionHistory: LiveData<List<Transaction>> =
        transactionRepository.getTransactionHistory().asLiveData()
//
//    fun loadTransactionDetail(transactionId: String) = liveData {
//        val detail = transactionRepository.getTransactionDetail(transactionId)
//        if (detail != null) {
//            emit(Result.success(detail)) // Ganti Result.Success dengan yang sesuai
//        } else {
//            emit(Result.failure(Exception("Transaction detail not found"))) // Ganti Result.Error
//        }
//    }


    suspend fun getTransactionDetailById(transactionId: String) =
        transactionRepository.getTransactionDetailById(transactionId)


    // LiveData untuk memuat data grafik (misalnya, 30 hari terakhir)
//    val dailySalesData: LiveData<List<DailySalesData>> = liveData {
//        // Logika untuk menentukan startTime dan endTime (misalnya, 30 hari lalu hingga hari ini)
//        val endTime = System.currentTimeMillis()
//        val startTime = endTime - (30L * 24 * 60 * 60 * 1000)
//
//        transactionRepository.getDailyRevenueForPeriod(startTime, endTime)
//            .collect { emit(it) }
//    }

}