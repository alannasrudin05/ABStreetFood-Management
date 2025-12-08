package com.praktikum.abstreetfood_management.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.RESTRICT // Jangan hapus User jika ada transaksi
        ),
        ForeignKey(
            entity = OutletEntity::class,
            parentColumns = ["id"],
            childColumns = ["outletId"],
            onDelete = ForeignKey.RESTRICT // Jangan hapus Outlet jika ada transaksi
        )
    ],
    indices = [
        Index(value = ["userId"]),   // Index untuk Foreign Key userId
        Index(value = ["outletId"]) // Index untuk Foreign Key outletId
    ]
)
data class TransactionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val outletId: String,

    // KOREKSI FINANSIAL:
    val subTotal: Double,       // BARU: Total item sebelum pajak/diskon
    val grandTotal: Double,

//    val paymentMethod: String,
//    val amountPaid: Double,
//    val changeAmount: Double = 0.0,

    val note: String,
    val transactionTime: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false,
    val syncTimestamp: Long = 0L
)