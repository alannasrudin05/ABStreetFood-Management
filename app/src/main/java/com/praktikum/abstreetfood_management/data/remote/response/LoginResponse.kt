package com.praktikum.abstreetfood_management.data.remote.response

import com.google.gson.annotations.SerializedName

// Harus dibuat agar Ktor/Gson dapat memparsing respons
//data class LoginResponse(
//    @SerializedName("message")
//    val message: String,
//    @SerializedName("user")
//    val user: UserDto // Respons user berada di dalam objek 'user'
//)

data class LoginResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("token") // <-- FIELD BARU
    val token: String,       // <-- FIELD KRUSIAL
    @SerializedName("user")
    val user: UserDto
)

