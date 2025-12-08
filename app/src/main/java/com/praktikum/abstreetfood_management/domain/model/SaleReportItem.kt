package com.praktikum.abstreetfood_management.domain.model

// Di domain/model/SaleReportItem.kt
data class SaleReportItem(
    val transactionId: String,
    val transactionTime: Long, // ⬅️ DITAMPUNG DI MODEL INI
    val productName: String,
    val quantity: Int, // <-- Diisi dari TI.quantity
    val itemPrice: Double, // <-- Diisi dari TI.itemPrice
    val totalItemRevenue: Double,
    val outletId: String
)