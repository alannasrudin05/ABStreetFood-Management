package com.praktikum.abstreetfood_management.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.praktikum.abstreetfood_management.data.local.entity.UserEntity
import com.praktikum.abstreetfood_management.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _users = MutableLiveData<List<UserEntity>>()
    val users: LiveData<List<UserEntity>> = _users

    fun loadUsers(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _users.value = repository.getUsers(forceRefresh)
        }
    }
}
