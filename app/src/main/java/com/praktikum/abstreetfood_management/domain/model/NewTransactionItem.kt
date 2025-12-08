package com.praktikum.abstreetfood_management.domain.model

data class NewTransactionItem(
    val productItemId: String,
    val productName: String,
    val variantName: String,
    val quantity: Int,
    val itemPrice: Double,
)