// data/local/entity/TransactionItemEntity.kt (BUAT FILE INI)
package com.praktikum.abstreetfood_management.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transaction_items",
    foreignKeys = [
        ForeignKey(
            entity = TransactionEntity::class,
            parentColumns = ["id"],
            childColumns = ["transactionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["productItemId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["transactionId"]),
        Index(value = ["productItemId"])
    ]
)
data class TransactionItemEntity(
    @PrimaryKey(autoGenerate = true)
    val itemId: Long = 0, // Primary Key unik untuk item
    val transactionId: String, // Menghubungkan ke TransactionEntity
    val productItemId: String,
    val quantity: Int,
    val itemPrice: Double, // Harga satuan item (termasuk ekstra nasi jika ada)
    val isSynced: Boolean = false
)