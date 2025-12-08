package com.praktikum.abstreetfood_management.domain.model

data class TransactionItem(
    val itemId: Long,
    val transactionId: String,
    val productItemId: String,
    val quantity: Int,
    val isExtraRice: Boolean,
    val itemPrice: Double
)