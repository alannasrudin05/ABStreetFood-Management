package com.praktikum.abstreetfood_management.domain

/**
 * User: Model Domain, digunakan oleh Use Case dan Presentation Layer (ViewModel/Fragment).
 * Objek ini bersih (tidak ada annotation Room/Ktor/Gson)
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val isActive: Boolean,
    val createdAt: Long
)