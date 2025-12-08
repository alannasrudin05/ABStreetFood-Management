package com.praktikum.abstreetfood_management.domain.model

data class StockItem(
    val id: String,
    val itemName: String,
    val currentStock: Double,
    val baseUnit: String,
    val conversionRateToPortion: Double,
    val estimatedPortionUnit: String,
    val minStockThreshold: Double,
    val isCritical: Boolean
)