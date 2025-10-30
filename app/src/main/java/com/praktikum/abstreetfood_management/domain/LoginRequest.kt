package com.praktikum.abstreetfood_management.domain

// Data class untuk request login (tidak perlu annotation)
data class LoginRequest(
    val email: String,
    val password: String
)