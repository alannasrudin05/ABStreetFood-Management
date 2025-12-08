package com.praktikum.abstreetfood_management.domain

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String

    // Role diatur default di server, tidak perlu dikirim
)