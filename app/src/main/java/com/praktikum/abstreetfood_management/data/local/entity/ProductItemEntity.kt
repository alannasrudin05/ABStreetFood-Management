package com.praktikum.abstreetfood_management.data.local.entity


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "product_item",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.RESTRICT // Jangan hapus Product jika masih ada varian
        )
    ],
    indices = [
        Index(value = ["productId"]),   // Index untuk Foreign Key userId
    ]
)
data class ProductItemEntity(
    @PrimaryKey val id: String,
    val productId: String,
//    apakah perlu ada image disini? perlu simpan ke database?
    val name: String,
    val sellingPrice: Double, // HARGA JUAL TOTAL (8000.0, 10000.0, 13000.0)
    val variantType: String, // 'TANPA_NASI', 'NASI_BIASA', 'NASI_DOUBLE'
    val skuCode: String,
    val lastSyncedAt: Long = System.currentTimeMillis()
)
