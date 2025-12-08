package com.praktikum.abstreetfood_management.data.mapper

import com.praktikum.abstreetfood_management.data.local.dao.TransactionWithItems
import com.praktikum.abstreetfood_management.data.local.entity.InventorySupplyEntity
import com.praktikum.abstreetfood_management.data.local.entity.OutletEntity
import com.praktikum.abstreetfood_management.domain.model.Product
import com.praktikum.abstreetfood_management.data.local.entity.ProductEntity
import com.praktikum.abstreetfood_management.data.local.entity.ProductItemEntity
import com.praktikum.abstreetfood_management.data.local.entity.RecipeEntity
import com.praktikum.abstreetfood_management.data.local.entity.ShiftEntity
import com.praktikum.abstreetfood_management.data.local.entity.StockItemEntity
import com.praktikum.abstreetfood_management.data.local.entity.TransactionEntity
import com.praktikum.abstreetfood_management.data.local.entity.TransactionItemEntity
import com.praktikum.abstreetfood_management.data.local.entity.UserEntity
import com.praktikum.abstreetfood_management.data.local.model.TransactionItemWithProduct
import com.praktikum.abstreetfood_management.data.remote.response.UserDto
import com.praktikum.abstreetfood_management.domain.model.InventorySupply
import com.praktikum.abstreetfood_management.domain.model.NewTransactionItem
import com.praktikum.abstreetfood_management.domain.model.Outlet
import com.praktikum.abstreetfood_management.domain.model.ProductItem
import com.praktikum.abstreetfood_management.domain.model.Recipe
import com.praktikum.abstreetfood_management.domain.model.Shift
import com.praktikum.abstreetfood_management.domain.model.StockItem
import com.praktikum.abstreetfood_management.domain.model.Transaction
import com.praktikum.abstreetfood_management.domain.model.TransactionDetail
import com.praktikum.abstreetfood_management.domain.model.User
import kotlin.Boolean

// ==============================================================================
// MAPPER USER ENTITY (Local/Remote Model) <-> USER DOMAIN (Business Model)
// ==============================================================================
//@Singleton
//class DataMapper @Inject constructor() {

    /**
     * Konversi UserEntity (Model Data Lokal) ke User (Model Domain)
     */
    fun UserEntity.toDomain(): User {
        return User(
            id = id,
            name = name,
            email = email,
            role = role,
            isActive = isActive
        )
    }

    /**
     * Konversi User (Model Domain) ke UserEntity (Model Data Lokal)
     * Memerlukan 'password' karena User Domain tidak menyimpannya.
     */
    fun User.toEntity(password: String): UserEntity {
        return UserEntity(
            id = id,
            name = name,
            email = email,
            password = password, // Password harus disediakan dari Use Case
            role = role,
            isActive = isActive,
            createdAt = System.currentTimeMillis(),
            lastSyncedAt = 0 // Tanda bahwa ini adalah entitas lokal baru
        )
    }

/**
 * Konversi UserDto (Model Remote) ke UserEntity (Model Data Lokal)
 * Membutuhkan password (teks biasa/hash) untuk mengisi kolom Entity yang kosong.
 */
fun UserDto.toEntity(password: String): UserEntity {
    return UserEntity(
        id = this.id,
        name = this.name, // Menggunakan 'nama' dari DTO untuk mengisi 'name' Entity
        email = this.email,
        password = password, // Gunakan password yang diinput user
        role = this.role,
        isActive = true, // Default saat login sukses dari Remote
        createdAt = System.currentTimeMillis(),
        lastSyncedAt = System.currentTimeMillis()
    )
}

/**
 * Konversi StockItemEntity (Model Data Lokal Bahan Baku) ke StockItem (Model Domain)
 */
fun StockItemEntity.toDomain(): StockItem {
    return StockItem(
        id = id,
        itemName = itemName,
        currentStock = currentStock,
        baseUnit = baseUnit,
        conversionRateToPortion = conversionRateToPortion,
        estimatedPortionUnit = estimatedPortionUnit,
        minStockThreshold = minStockThreshold,
        isCritical = isCritical // Menggunakan property helper dari Entity
    )
}
fun StockItem.toEntity(): StockItemEntity {
    return StockItemEntity(
        id = id,
        itemName = itemName,
        currentStock = currentStock,
        baseUnit = baseUnit,
        conversionRateToPortion = conversionRateToPortion,
        estimatedPortionUnit = estimatedPortionUnit,
        minStockThreshold = minStockThreshold,
        isSynced = false,
//        isCritical = isCritical
    )
}

// [TAMBAHAN OPSIONAL] Untuk getAllProducts di ViewModel
/**
 * Konversi ProductEntity ke Product Domain Model
 */
fun ProductEntity.toDomain(): Product { // Asumsi Anda punya Product data class di domain
    return Product(
        id = id,
        name = name,
        isActive = isActive
    )
}
/**
 * Konversi Product (Domain) ke ProductEntity (Lokal)
 */
fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        name = name,
        isActive = true // Asumsi produk baru aktif
    )
}

/**
 * Konversi ProductItem (Domain) ke ProductItemEntity (Lokal)
 */
fun ProductItem.toEntity(): ProductItemEntity {
    // Asumsi properti di ProductItem sama dengan ProductItemEntity (kecuali lastSyncedAt)
    return ProductItemEntity(
        id = id,
        productId = productId,
        name = name,
        sellingPrice = sellingPrice,
        variantType = variantType,
        skuCode = "SKU-001",
        lastSyncedAt = System.currentTimeMillis()
    )
}

/**
 * Konversi ProductItemEntity (Lokal) ke ProductItem (Domain)
 */
fun ProductItemEntity.toDomain(): ProductItem {
    // Asumsi Domain Model ProductItem hanya berisi properti inti
    return ProductItem(
        id = id,
        productId = productId,
        name = name,
        sellingPrice = sellingPrice,
        variantType = variantType
    )
}


// ==============================================================================
// MAPPER TRANSAKSI & SHIFT
// ==============================================================================

/**
 * Konversi TransactionEntity (Transaksi Lokal) ke Transaction (Domain)
 * (Asumsi Anda perlu data class Transaction di domain)
 */
fun TransactionEntity.toDomain(): Transaction {
    return Transaction(
        id = id,
        outletId = outletId,
        userId = userId,
        subTotal = subTotal,
        grandTotal = grandTotal,
        note = note,
        transactionTime = transactionTime,
//        isSynced = isSynced
    )
}

/**
 * Konversi Transaction (Domain) ke TransactionEntity (Lokal)
 * (Digunakan saat mencatat transaksi baru)
 */
fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        outletId = outletId,
        userId = userId,
        subTotal = subTotal,
        grandTotal = grandTotal,
        note = note,
        transactionTime = transactionTime,
//        isSynced = false,
        syncTimestamp = System.currentTimeMillis() // Set timestamp saat dibuat
    )
}

/**
 * Konversi NewTransactionItem (Domain/Input Keranjang) ke TransactionItemEntity (Lokal)
 * Membutuhkan transactionId dari transaksi induk.
 */
fun NewTransactionItem.toEntity(transactionId: String): TransactionItemEntity {
    return TransactionItemEntity(
        itemId = 0, // Dibiarkan 0 karena PK AutoGenerate Room
        transactionId = transactionId,
        productItemId = productItemId,
        quantity = quantity,
        itemPrice = itemPrice,
        isSynced = false // Selalu false saat pertama kali dibuat
    )
}

/**
 * Konversi TransactionItemEntity (Lokal) kembali ke NewTransactionItem (Domain/Display Nota)
 * Membutuhkan nama produk dan varian yang harus didapatkan melalui JOIN atau lookup terpisah
 * di layer Repository/DAO untuk display yang informatif di nota.
 */
//fun TransactionItemEntity.toDomainNota(productName: String, variantName: String): NewTransactionItem {
//    return NewTransactionItem(
//        productItemId = productItemId,
//        productName = productName, // Disuplai oleh Repository/Join Query
//        variantName = variantName, // Disuplai oleh Repository/Join Query
//        quantity = quantity,
//        itemPrice = itemPrice
//    )
//}


//fun TransactionItemEntity.toDomainNewItem(productName: String, variantName: String): NewTransactionItem {
//    return NewTransactionItem(
//        productItemId = productItemId,
//        productName = productName, // Disuplai oleh Repository/Join Query
//        variantName = variantName, // Disuplai oleh Repository/Join Query
//        quantity = quantity,
//        itemPrice = itemPrice
//    )
//}
//
//fun TransactionItemEntity.toDomainNewItemBase(): NewTransactionItem {
//    return NewTransactionItem(
//        productItemId = productItemId,
//        productName = productName,
//        variantName = variantName,
//        quantity = quantity,
//        itemPrice = itemPrice
//    )
//}

//fun TransactionWithItems.toDomainDetail(): TransactionDetail {
//    // Asumsi TransactionEntity punya toDomain() yang mengembalikan Transaction
//    // Asumsi TransactionItemEntity punya toDomainNewItem() yang mengembalikan NewTransactionItem
//    return TransactionDetail(
//        header = this.transaction.toDomain(),
//        items = this.items.map { it.toDomainNewItem() }
//    )
//}

fun TransactionItemWithProduct.toDomainNewItem(): NewTransactionItem {
    return NewTransactionItem(
        productItemId = this.productItemId,
        productName = this.productName,   // ✅ Mengambil dari hasil JOIN
        variantName = this.variantName,   // ✅ Mengambil dari hasil JOIN
        quantity = this.quantity,
        itemPrice = this.itemPrice
    )
}

/**
 * Konversi ShiftEntity (Shift Lokal) ke Shift (Domain)
 * (Asumsi Anda perlu data class Shift di domain)
 */
fun ShiftEntity.toDomain(): Shift {
    return Shift(
        id = id,
        userId = userId,
        outletId = outletId,
        startTime = startTime,
        endTime = endTime,
        startCash = startCash,
        endCashActual = endCashActual,
        isClosed = isClosed
    )
}

/**
 * Konversi Shift (Domain) ke ShiftEntity (Lokal)
 * (Digunakan saat membuka atau menutup shift)
 */
fun Shift.toEntity(): ShiftEntity {
    return ShiftEntity(
        id = id,
        userId = userId,
        outletId = outletId,
        startTime = startTime,
        endTime = endTime,
        startCash = startCash,
        endCashActual = endCashActual,
        isClosed = isClosed
    )
}

// ==============================================================================
// MAPPER OUTLET & SUPPLY
// ==============================================================================

/**
 * Konversi OutletEntity (Lokal) ke Outlet (Domain)
 * (Asumsi Anda perlu data class Outlet di domain)
 */
fun OutletEntity.toDomain(): Outlet {
    return Outlet(
        id = id,
        name = name,
        location = location,
        estimatedStock = estimatedStock
    )
}

/**
 * Konversi InventorySupplyEntity (Lokal) ke InventorySupply (Domain)
 * (Asumsi Anda perlu data class InventorySupply di domain)
 */
fun InventorySupplyEntity.toDomain(): InventorySupply {
    return InventorySupply(
        id = id,
        outletId = outletId,
        stockItemId = stockItemId,
        quantity = quantity,
        supplyDate = supplyDate,
        recordedByUserId = recordedByUserId,
    )
}

/**
 * Konversi RecipeEntity (Lokal) ke Recipe (Domain)
 * (Digunakan oleh StockUseCase untuk menghitung bahan yang terpakai)
 */
fun RecipeEntity.toDomain(): Recipe {
    return Recipe(
        id = id,
        productItemId = productItemId,
        stockItemId = stockItemId,
        requireQuantity = requireQuantity,
        lastSyncedAt = System.currentTimeMillis()
    )
}
fun Recipe.toEntity(): RecipeEntity {
    return RecipeEntity(
        id = id,
        productItemId = productItemId,
        stockItemId = stockItemId,
        requireQuantity = requireQuantity,
//        lastSyncedAt = System.currentTimeMillis() // Set timestamp saat dibuat/diubah
    )
}

