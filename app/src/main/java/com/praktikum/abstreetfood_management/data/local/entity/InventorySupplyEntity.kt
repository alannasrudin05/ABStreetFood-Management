package com.praktikum.abstreetfood_management.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "inventory_supply",
    foreignKeys = [
        ForeignKey(
            entity = StockItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["stockItemId"],
            onDelete = ForeignKey.RESTRICT // Jangan hapus Bahan jika ada riwayat suplai
        ),
        ForeignKey(
            entity = OutletEntity::class,
            parentColumns = ["id"],
            childColumns = ["outletId"],
            onDelete = ForeignKey.RESTRICT // Jangan hapus Outlet jika ada riwayat suplai
        )
    ],
    indices = [
        Index(value = ["stockItemId"]),
        Index(value = ["outletId"])
    ]
)
data class InventorySupplyEntity(
    @PrimaryKey val id: String,
    val stockItemId: String,
    val outletId: String,
    val quantity: Double,
    val supplyDate: Long, // Asumsi Admin yang buat, langsung sync
    val recordedByUserId: String,
    val lastSyncedAt: Long = System.currentTimeMillis()
)
