package com.praktikum.abstreetfood_management.data.remote

import com.praktikum.abstreetfood_management.data.network.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // URL VERCEL ANDA
    private const val BASE_URL = "https://abs-backend-drab.vercel.app/"

    // 1. BUAT LOGGER
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        // Atur level logging: BODY menunjukkan semua header dan body request/response
        // Gunakan Level.BASIC atau Level.HEADERS jika ingin lebih ringkas
        level = HttpLoggingInterceptor.Level.BODY
    }

    // 2. BUAT HTTP CLIENT dengan LOGGER
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        // Opsional: Atur Timeout
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}