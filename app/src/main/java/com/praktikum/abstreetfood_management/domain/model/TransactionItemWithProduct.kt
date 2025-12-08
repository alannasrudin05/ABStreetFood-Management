package com.praktikum.abstreetfood_management.data.local.model

// Menggabungkan data dari TransactionItemEntity dan ProductItemEntity
data class TransactionItemWithProduct(
    // Dari TransactionItemEntity
    val transactionId: String,
    val productItemId: String,
    val quantity: Int,          // Ubah ke Int jika sesuai entitas
    val itemPrice: Double,

    // Dari ProductItemEntity (hasil JOIN)
    val productName: String,    // Dari P.name di JOIN
    val variantName: String     // Dari P.variantName di JOIN
)