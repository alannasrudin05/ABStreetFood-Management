package com.praktikum.abstreetfood_management.domain.model

data class InventorySupply(
    val id: String,
    val stockItemId: String,
    val outletId: String,
    val quantity: Double,
    val supplyDate: Long, // Menggunakan Long (Timestamp) untuk kemudahan bisnis
    val recordedByUserId: String // Menggunakan Long (Timestamp) untuk kemudahan bisnis
)