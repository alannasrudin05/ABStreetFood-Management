//package com.praktikum.abstreetfood_management.data.repository
//
//import com.praktikum.abstreetfood_management.data.local.entity.UserEntity
//import com.praktikum.abstreetfood_management.domain.LoginRequest
//import com.praktikum.abstreetfood_management.domain.model.User
//import kotlinx.coroutines.flow.Flow
//
//interface IAuthRepository {
//    // Auth & User Management
//    suspend fun login(request: LoginRequest): Result<User>
//    fun getLoggedInUser(): Flow<UserEntity?>
//
//    // Sync
//    suspend fun syncUsersFromRemote(): Boolean
//}