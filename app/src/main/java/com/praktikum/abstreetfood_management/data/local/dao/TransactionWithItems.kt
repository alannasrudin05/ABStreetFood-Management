package com.praktikum.abstreetfood_management.data.local.dao

import androidx.room.Embedded
import androidx.room.Relation
import com.praktikum.abstreetfood_management.data.local.entity.TransactionEntity
import com.praktikum.abstreetfood_management.data.local.entity.TransactionItemEntity


data class TransactionWithItems(
    @Embedded val transaction: TransactionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "transactionId"
    )
    val items: List<TransactionItemEntity>
)