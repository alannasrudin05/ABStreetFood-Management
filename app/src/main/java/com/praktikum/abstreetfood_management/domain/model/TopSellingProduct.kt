package com.praktikum.abstreetfood_management.domain.model

data class TopSellingProduct(
    val name: String,
//    val productItem: String?, //untuk ngambil image
    val totalQuantitySold: Int,
    val transactionTime: Long?,
//    sepertinya nanti diambil TopSellingProduct di tanggal hari ini dan di tampilkan. Tanggalnya bisa diambil dari transactionTime di Transaction
    val totalRevenue: Double,
)