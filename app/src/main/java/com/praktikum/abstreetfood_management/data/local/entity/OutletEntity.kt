package com.praktikum.abstreetfood_management.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "outlet")
data class OutletEntity (
    @PrimaryKey val id: String,
    val name: String,
    val location: String,
    val estimatedStock: Double,

)


//
//data class UserEntity(
//    val password: String, // Dalam produksi, hash password!
//    val role: String = "user", // admin, cashier, user
//    val isActive: Boolean = true,
//    val createdAt: Long = System.currentTimeMillis(),
//    val lastSyncedAt: Long = System.currentTimeMillis()
//)
