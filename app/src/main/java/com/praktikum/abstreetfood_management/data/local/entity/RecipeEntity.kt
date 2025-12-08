package com.praktikum.abstreetfood_management.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date


@Entity(
    tableName = "recipe",
    foreignKeys = [
        ForeignKey(
            entity = ProductItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["productItemId"],
            onDelete = ForeignKey.CASCADE // Jika Varian Produk dihapus, Resep ikut dihapus
        ),
        ForeignKey(
            entity = StockItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["stockItemId"],
            onDelete = ForeignKey.RESTRICT // Jangan hapus Bahan Baku jika masih terdaftar di resep
        )
    ],
    indices = [
        Index(value = ["productItemId"]),   // Index untuk Foreign Key userId
        Index(value = ["stockItemId"]) // Index untuk Foreign Key outletId
    ]
)
data class RecipeEntity(
    @PrimaryKey val id: String,
    val productItemId: String,
    val stockItemId: String,
    val requireQuantity: Double,
    val lastSyncedAt: Long = System.currentTimeMillis()
)
