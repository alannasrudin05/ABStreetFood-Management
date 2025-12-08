package com.praktikum.abstreetfood_management.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val isActive: Boolean = true,
    val isSynced: Boolean = true, // Asumsi Admin yang buat, langsung sync
    val lastSyncedAt: Long = System.currentTimeMillis()
)