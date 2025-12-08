package com.praktikum.abstreetfood_management.domain.model

data class Recipe(
    val id: String,
    val productItemId: String,
    val stockItemId: String,
    val requireQuantity: Double,
    val lastSyncedAt: Long
)