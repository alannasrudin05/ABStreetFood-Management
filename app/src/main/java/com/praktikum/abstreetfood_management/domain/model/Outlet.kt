package com.praktikum.abstreetfood_management.domain.model

data class Outlet(
    val id: String,
    val name: String,
    val location: String,
    val estimatedStock: Double
)