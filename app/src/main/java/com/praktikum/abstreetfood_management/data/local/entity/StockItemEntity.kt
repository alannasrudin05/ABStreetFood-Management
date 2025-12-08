package com.praktikum.abstreetfood_management.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stock_items")
data class StockItemEntity(
    @PrimaryKey val id: String,
    val itemName: String,
    val currentStock: Double, // Stok Fisik Master (e.g., 50.0 Kg)
    val baseUnit: String,     // <<< BARU: Unit dasar stok (e.g., "Kg", "Liter")

    // Kunci untuk Konversi ke Porsi:
    val conversionRateToPortion: Double, // <<< BARU: Berapa porsi dihasilkan dari 1 unit dasar (e.g., 1 Kg Beras = 5.0 porsi)
    val estimatedPortionUnit: String,    // <<< BARU: Unit yang dilihat Kasir (e.g., "Porsi")

    val minStockThreshold: Double,
    val isSynced: Boolean = false
) {
    // Properti helper untuk menentukan apakah stok kritis (digunakan di ViewModel)
    val isCritical: Boolean
        get() = currentStock <= minStockThreshold
}