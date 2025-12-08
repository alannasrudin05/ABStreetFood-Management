package com.praktikum.abstreetfood_management.domain.model

data class NewTransaction(
    val userId: String,
    val outletId: String,

    val subTotal: Double,       // BARU: Total item sebelum pajak/diskon
    val grandTotal: Double,     // KOREKSI: Total Akhir (totalAmount -> grandTotal)

    val note: String,
    val transactionTime: Long = System.currentTimeMillis(),

    val items: List<NewTransactionItem>
)