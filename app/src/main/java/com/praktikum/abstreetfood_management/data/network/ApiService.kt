package com.praktikum.abstreetfood_management.data.network

import com.praktikum.abstreetfood_management.data.remote.response.LoginResponse
import com.praktikum.abstreetfood_management.data.remote.response.UserDto
import com.praktikum.abstreetfood_management.domain.LoginRequest
import com.praktikum.abstreetfood_management.domain.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.Response

interface ApiService {
    // 1. Endpoint untuk mendapatkan data (misalnya: GET /api/foods)
//    @GET("api/foods")
//    suspend fun getFoodItems(): Response<ApiResponse>

    // 2. Endpoint untuk login (misalnya: POST /api/login)
    @POST("auth/login")
    suspend fun loginUser(@Body requestBody: LoginRequest): retrofit2.Response<LoginResponse>

    @POST("auth/register")
    suspend fun registerUser(@Body requestBody: RegisterRequest): Response<UserDto>
    // 3. Endpoint yang memerlukan Token Otentikasi
//    @GET("api/profile")
//    suspend fun getUserProfile(@Header("Authorization") token: String): Response<UserProfile>
}