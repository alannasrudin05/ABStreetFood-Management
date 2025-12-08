//package com.praktikum.abstreetfood_management.data.mapper
//
//import com.praktikum.abstreetfood_management.data.local.entity.UserEntity
//import com.praktikum.abstreetfood_management.domain.model.User
//
///**
// * UserMapper: Berfungsi untuk mengkonversi objek antar Layer.
// * Entity (Local/Room) -> Domain Model (Use Case/Presentation)
// * Domain Model -> Entity (Local/Room)
// */
//
//// Konversi dari Entity (Room) ke Domain Model
//fun UserEntity.toDomain(): User {
//    return User(
//        id = id,
//        name = name,
//        email = email,
//        role = role,
//        isActive = isActive,
//        createdAt = createdAt
//    )
//}
//
//// Konversi dari Domain Model ke Entity (Room)
//fun User.toEntity(passwordHash: String): UserEntity {
//    return UserEntity(
//        id = id,
//        name = name,
//        email = email,
//        password = passwordHash, // Catatan: Password HANYA ada di Entity saat disimpan
//        role = role,
//        isActive = isActive,
//        // createdAt dan lastSyncedAt akan di-handle default oleh Entity
//    )
//}
