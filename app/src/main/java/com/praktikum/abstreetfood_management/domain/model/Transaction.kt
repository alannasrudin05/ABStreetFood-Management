package com.praktikum.abstreetfood_management.domain.model

data class Transaction(
    val id: String,
    val userId: String,
    val outletId: String,
    val subTotal: Double,
    val grandTotal: Double,
    val note: String,
    val transactionTime: Long
)