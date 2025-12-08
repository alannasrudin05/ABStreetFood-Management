package com.praktikum.abstreetfood_management.data.remote.response

import com.google.gson.annotations.SerializedName

// DTO untuk data user yang diterima dari API
data class UserDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("name") // Sesuai dengan properti 'name' di JSON
    val name: String,
    @SerializedName("role")
    val role: String
    // Kolom lain seperti isActive tidak dikirim di respons login, jadi tidak perlu di sini
)