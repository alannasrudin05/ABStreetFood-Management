package com.praktikum.abstreetfood_management.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "shifts",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.RESTRICT // Jangan hapus User jika masih ada Shift
        ),
        ForeignKey(
            entity = OutletEntity::class,
            parentColumns = ["id"],
            childColumns = ["outletId"],
            onDelete = ForeignKey.RESTRICT // Jangan hapus Outlet jika ada Shift
        )
    ],
    indices = [
        Index(value = ["userId"]),   // Index untuk Foreign Key userId
        Index(value = ["outletId"]) // Index untuk Foreign Key outletId
    ]
)
data class ShiftEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val outletId: String,
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val startCash: Double,
    val endCashActual: Double? = null,
    val isClosed: Boolean = false,
    val isSynced: Boolean = false
)