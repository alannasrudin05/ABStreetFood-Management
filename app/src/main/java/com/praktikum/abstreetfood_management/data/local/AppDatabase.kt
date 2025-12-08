// data/local/AppDatabase.kt (Koreksi Final)
package com.praktikum.abstreetfood_management.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
// ... (imports lainnya) ...
import com.praktikum.abstreetfood_management.data.local.dao.* // Import semua DAO baru
import com.praktikum.abstreetfood_management.data.local.entity.* // Import semua Entity baru
import com.praktikum.abstreetfood_management.utility.PasswordHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

// KOREKSI UTAMA 1: NAIKKAN VERSI DATABASE!
@Database(
    entities = [
        UserEntity::class, ProductEntity::class, TransactionEntity::class, ShiftEntity::class,
        TransactionItemEntity::class, StockItemEntity::class, ProductItemEntity::class,
        InventorySupplyEntity::class, RecipeEntity::class, OutletEntity::class
    ],
    version = 2, // <<< HARUS DITAIKKAN (misalnya 5) karena banyak Entity baru
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    // ... (Abstract DAO functions) ...
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun transactionDao(): TransactionDao
    abstract fun shiftDao(): ShiftDao
    abstract fun transactionItemDao(): TransactionItemDao
    abstract fun stockItemDao(): StockItemDao
    abstract fun productItemDao(): ProductItemDao
    abstract fun recipeDao(): RecipeDao
    abstract fun outletDao(): OutletDao
    // Tambahkan DAO baru di sini (Outlet, InventorySupply)

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        const val OUTLET_ID = "outlet_pusat_001"
        private lateinit var passwordHelper: PasswordHelper


        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                // Inisialisasi PasswordHelper (Harus diinisialisasi sebelum dikirim ke Callback)
                if (!this::passwordHelper.isInitialized) {
                    passwordHelper = PasswordHelper()
                }

                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "absfood"
                )
                    .fallbackToDestructiveMigration()
                    // Mengirim PasswordHelper ke Callback
                    .addCallback(DatabaseCallback(passwordHelper))
                    .build().also { INSTANCE = it }
            }
    }

    // KOREKSI UTAMA 2: Callback menerima PasswordHelper
    private class DatabaseCallback(private val passwordHelper: PasswordHelper) : RoomDatabase.Callback() {

        // KOREKSI 3: onCreate sekarang menggunakan PasswordHelper
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                // Jalankan seeding di Coroutine Scope
                CoroutineScope(Dispatchers.IO).launch {

//                    val adminId = seedUsers(database.userDao(), passwordHelper) // Kirim helper
                    val outletId = seedOutlet(database.outletDao()) // Ambil ID Outlet
                    val (ag_item2, ab_item5) = seedProducts(database.productDao(), database.productItemDao())

//                    seedTransactions(database.transactionDao(), database.transactionItemDao(), adminId, outletId, ag_item2, ab_item5)
                    seedStockItems(database.stockItemDao())
                    // TODO: seedInventorySupply dan seedRecipes setelah DAO-nya dibuat
                }
            }
        }

        /**
         * 1. Fungsi untuk menyuntikkan data user uji.
         * KOREKSI: Menerima PasswordHelper sebagai argumen dan menggunakannya.
         */
        private suspend fun seedUsers(userDao: UserDao, passwordHelper: PasswordHelper): String {
            val adminId = UUID.randomUUID().toString()
            val cashierId = UUID.randomUUID().toString()

//            val adminId = "USER_ADMIN_001"
//            val cashierId = "USER_KASIR_001"

            // Data Uji 1: Admin
//            val adminUser = UserEntity(
//                id = adminId,
//                name = "Admin Uji",
//                email = "admin@test.com",
//                password = passwordHelper.hashPassword("password123"), // ✅ Hashed
//                role = "admin"
//            )
//
//            // Data Uji 2: Cashier
//            val cashierUser = UserEntity(
//                id = cashierId,
//                name = "Kasir Toko",
//                email = "kasir@test.com",
//                password = passwordHelper.hashPassword("kasir123"), // ✅ Hashed
//                role = "cashier"
//            )

            val adminUser = UserEntity(
                id = adminId,
                name = "Admin Uji",
                email = "admin@test.com",
                // GANTI INI DENGAN HASH TURSO (string literal)
                password = "ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f",
                role = "admin"
            )

            // Data Uji 2: Cashier
            val cashierUser = UserEntity(
                id = cashierId,
                name = "Kasir Toko",
                email = "kasir@test.com",
                // GANTI INI DENGAN HASH TURSO (string literal)
                password = "f02b7c1e519e4fa436147f7e1399974f9510aa9c8e0cb8be29151eb540f9d214",
                role = "cashier"
            )

//            userDao.insertUser(adminUser)
//            userDao.insertUser(cashierUser)
            return adminId
        }

        /**
         * 2. Fungsi untuk menyuntikkan data Outlet uji.
         * KOREKSI: Menggunakan atribut final (tanpa estimatedStock, tambah taxRate, isActive).
         */
        private suspend fun seedOutlet(outletDao: OutletDao): String {
            val outletId = OUTLET_ID
            val outlet = OutletEntity(
                id = outletId,
                name = "AB Street Food Pusat",
                location = "Jl. Sudirman No. 1",
                estimatedStock = 10.0,
//                taxRate = 0.10, // 10%
//                isActive = true
            )
            // Asumsi Anda sudah punya OutletDao.insertOutlet(outlet)
            // OutletDao harus di-import
             outletDao.insertOutlet(outlet) // Ganti
            return outletId
        }

        /**
         * 3. Fungsi untuk menyuntikkan data produk dan varian uji.
         * KOREKSI: Hapus atribut price/stock dari ProductEntity. Hapus basePrice dari ProductItemEntity.
         */
        private suspend fun seedProducts(productDao: ProductDao, productItemDao: ProductItemDao): Pair<ProductItemEntity, ProductItemEntity> {
            val productAyamGorengId = UUID.randomUUID().toString()
            val productAyamBakarId = UUID.randomUUID().toString()

            // 1. Produk Utama (ProductEntity) - SIMPLIFIKASI
            val productAyamGoreng = ProductEntity(id = productAyamGorengId, name = "Ayam Goreng", isActive = true, isSynced = true)
            val productAyamBakar = ProductEntity(id = productAyamBakarId, name = "Ayam Bakar", isActive = true, isSynced = true)
            productDao.insertProducts(listOf(productAyamGoreng, productAyamBakar))

            // 2. Varian Produk (ProductItemEntity) - FOKUS HARGA JUAL & SKU

            // AYAM GORENG VARIAN
            val ag_item1 = ProductItemEntity(id = UUID.randomUUID().toString(), productId = productAyamGorengId, name = "Ayam Goreng", sellingPrice = 7000.0, variantType = "TANPA_NASI", skuCode = "AG-01")
            val ag_item2 = ProductItemEntity(id = UUID.randomUUID().toString(), productId = productAyamGorengId, name = "Ayam Goreng", sellingPrice = 10000.0, variantType = "NASI_BIASA", skuCode = "AG-02")
            val ag_item3 = ProductItemEntity(id = UUID.randomUUID().toString(), productId = productAyamGorengId, name = "Ayam Goreng", sellingPrice = 13000.0, variantType = "NASI_DOUBLE", skuCode = "AG-03")

            // AYAM BAKAR VARIAN
            val ab_item4 = ProductItemEntity(id = UUID.randomUUID().toString(), productId = productAyamBakarId, name = "Ayam Bakar", sellingPrice = 7000.0, variantType = "TANPA_NASI", skuCode = "AB-01")
            val ab_item5 = ProductItemEntity(id = UUID.randomUUID().toString(), productId = productAyamBakarId, name = "Ayam Bakar", sellingPrice = 10000.0, variantType = "NASI_BIASA", skuCode = "AB-02")
            val ab_item6 = ProductItemEntity(id = UUID.randomUUID().toString(), productId = productAyamBakarId, name = "Ayam Bakar", sellingPrice = 13000.0, variantType = "NASI_DOUBLE", skuCode = "AB-03")

            val allItems = listOf(ag_item1, ag_item2, ag_item3, ab_item4, ab_item5, ab_item6)
//            productItemDao.insertProductItem(allItems.first()) // Hanya untuk menghindari error, harusnya insertProducts
            productItemDao.insertProductItems(allItems)
            return Pair(ag_item2, ab_item5)
//            return allItems
        }

        /**
         * 4. Fungsi untuk menyuntikkan data Stok Bahan Baku uji.
         * KOREKSI: Menggunakan Double dan atribut konversi baru.
         */
        private suspend fun seedStockItems(stockItemDao: StockItemDao) {
            val ayam = StockItemEntity(
                id = "stk_ayam",
                itemName = "Ayam Utuh",
                currentStock = 100.0, // 100 Kg di Master
                baseUnit = "Kg",
                conversionRateToPortion = 3.0, // 1 Kg Ayam -> 3 Porsi
                estimatedPortionUnit = "Porsi",
                minStockThreshold = 10.0 // Kritis di 10 Kg
            )
            val beras = StockItemEntity(
                id = "stk_beras",
                itemName = "Beras",
                currentStock = 50.0, // 50 Kg di Master
                baseUnit = "Kg",
                conversionRateToPortion = 5.0, // 1 Kg Beras -> 5 Porsi Nasi
                estimatedPortionUnit = "Porsi Nasi",
                minStockThreshold = 5.0 // Kritis di 5 Kg
            )
            // Asumsi Anda punya StockItemDao.insertStockItems(listOf(ayam, beras))
            // database.stockItemDao().insertStockItems(listOf(ayam, beras))
        }


        /**
         * 5. Fungsi untuk menyuntikkan data transaksi uji.
         * KOREKSI: Menggunakan atribut finansial baru (subTotal, taxAmount, discountAmount, grandTotal).
         */
        private suspend fun seedTransactions(
            transactionDao: TransactionDao,
            transactionItemDao: TransactionItemDao,
            userId: String,
            outletId: String,
            ag_item2: ProductItemEntity, // Ayam Goreng (Nasi Biasa)
            ab_item5: ProductItemEntity  // Ayam Bakar (Nasi Biasa)
        ) {
            val txId = UUID.randomUUID().toString()

            // Item 1: 1 x 13000 = 13000
            // Item 2: 1 x 13000 = 13000
            val subTotal = 26000.0
            val taxRate = 0.10 // Asumsi Outlet taxRate = 10%
            val discountAmount = 0.0
            val taxAmount = subTotal * taxRate
            val grandTotal = subTotal + taxAmount - discountAmount // 26000 + 2600 = 28600

            // 1. Transaction Entity
            val transaction = TransactionEntity(
                id = txId,
                userId = userId,
                outletId = outletId,
                subTotal = subTotal,
                grandTotal = grandTotal,
                note = "Transaksi Uji Seeder",
                transactionTime = System.currentTimeMillis() - 3600000L, // 1 jam lalu
                isSynced = false
            )
            transactionDao.insertTransaction(transaction)

            // 2. Transaction Item Entities
            val item1 = TransactionItemEntity(
                transactionId = txId,
                productItemId = ag_item2.id,
                quantity = 1,
                itemPrice = ag_item2.sellingPrice,
                isSynced = false
            )
            val item2 = TransactionItemEntity(
                transactionId = txId,
                productItemId = ab_item5.id,
                quantity = 1,
                itemPrice = ab_item5.sellingPrice,
                isSynced = false
            )

            // Menggunakan insertTransactionItems
            transactionItemDao.insertTransactionItems(listOf(item1, item2))
        }

        // Asumsi fungsi seedStockItems dan seedTransactions juga ada
    }

}


//package com.praktikum.abstreetfood_management.data.local
//
//import android.content.Context
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//import androidx.sqlite.db.SupportSQLiteDatabase
//import com.praktikum.abstreetfood_management.data.local.dao.ProductDao
//import com.praktikum.abstreetfood_management.data.local.dao.ProductItemDao
//import com.praktikum.abstreetfood_management.data.local.dao.RecipeDao
//import com.praktikum.abstreetfood_management.data.local.dao.ShiftDao
//import com.praktikum.abstreetfood_management.data.local.dao.StockItemDao
//import com.praktikum.abstreetfood_management.data.local.dao.TransactionDao
//import com.praktikum.abstreetfood_management.data.local.dao.TransactionItemDao
//import com.praktikum.abstreetfood_management.data.local.dao.UserDao
//import com.praktikum.abstreetfood_management.data.local.entity.InventorySupplyEntity
//import com.praktikum.abstreetfood_management.data.local.entity.OutletEntity
//import com.praktikum.abstreetfood_management.data.local.entity.ProductEntity
//import com.praktikum.abstreetfood_management.data.local.entity.ProductItemEntity
//import com.praktikum.abstreetfood_management.data.local.entity.RecipeEntity
//import com.praktikum.abstreetfood_management.data.local.entity.ShiftEntity
//import com.praktikum.abstreetfood_management.data.local.entity.StockItemEntity
//import com.praktikum.abstreetfood_management.data.local.entity.TransactionEntity
//import com.praktikum.abstreetfood_management.data.local.entity.TransactionItemEntity
//import com.praktikum.abstreetfood_management.data.local.entity.UserEntity
//import com.praktikum.abstreetfood_management.utility.PasswordHelper
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import java.util.UUID
//
////@Database(entities = [UserEntity::class], version = 1, exportSchema = false)
//@Database(
//    entities = [
//        UserEntity::class,
//        ProductEntity::class,
//        TransactionEntity::class,
//        ShiftEntity::class,
//        TransactionItemEntity::class,
//        StockItemEntity::class,
//        ProductItemEntity::class,
//        InventorySupplyEntity::class,
//        RecipeEntity::class,
//        OutletEntity::class
//    ],
//    version = 1, // Naikkan versi!
//    exportSchema = true
//)
//abstract class AppDatabase : RoomDatabase() {
//    abstract fun userDao(): UserDao
//    abstract fun productDao(): ProductDao
//    abstract fun transactionDao(): TransactionDao
//    abstract fun shiftDao(): ShiftDao
//    abstract fun transactionItemDao(): TransactionItemDao
//    abstract fun stockItemDao(): StockItemDao
//    abstract fun productItemDao(): ProductItemDao
//    abstract fun recipeDao(): RecipeDao
//
//
//
//    companion object {
//        @Volatile private var INSTANCE: AppDatabase? = null
//        const val OUTLET_ID = "outlet_pusat_001"
//        private lateinit var passwordHelper: PasswordHelper
//
//
////        fun getInstance(context: Context): AppDatabase =
////            INSTANCE ?: synchronized(this) {
////                INSTANCE ?: Room.databaseBuilder(
////                    context.applicationContext,
////                    Ap pDatabase::class.java, "app_database"
////                ).fallbackToDestructiveMigration().build()
////            }
//
//fun getInstance(context: Context): AppDatabase =
//    INSTANCE ?: synchronized(this) {
//        if (!this::passwordHelper.isInitialized) {
//            passwordHelper = PasswordHelper()
//        }
//
//        INSTANCE ?: Room.databaseBuilder(
//            context.applicationContext,
//            AppDatabase::class.java, "absfood"
//        )
//            .fallbackToDestructiveMigration()
//            .addCallback(DatabaseCallback(context)) // Tambahkan callback untuk data uji
//            .build().also { INSTANCE = it }
//    }
//    }
//    /**
//     * Callback untuk menjalankan data uji (seeder) saat database dibuat.
//     */
//    private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
//
//
//        override fun onCreate(db: SupportSQLiteDatabase) {
//            super.onCreate(db)
//            INSTANCE?.let { database ->
//                // Jalankan seeding di Coroutine Scope
//                CoroutineScope(Dispatchers.IO).launch {
//
////                    seedUsers(database.userDao())
//                    val adminId = seedUsers(database.userDao()) // Ambil ID Admin
//                    seedOutlet(database) // Seed Outlet (Wajib karena FK)
//                    val (agId, abId, item2, item5) = seedProducts(database.productDao(), database.productItemDao())
//                    seedTransactions(database.transactionDao(), database.transactionItemDao(), adminId, item2.id, item5.id)
//
////                    seedProducts(database.productDao(), database.productItemDao())
////                    seedProducts(database.productDao())        // <<< BARU
////                    seedStockItems(database.stockItemDao())    // <<< BARU
////                    seedRecipes(database.recipeDao())          // <<< BARU
////                    seedInventorySupply(database.inventorySupplyDao()) // <<< BARU
////                    seedTransactions(database.transactionDao(), database.transactionItemDao())
//                }
//            }
//        }
//
//        private suspend fun seedOutlet(database: AppDatabase) {
//            val outlet = OutletEntity(
//                id = OUTLET_ID,
//                name = "AB Street Food Pusat",
//                location = "Jl. Sudirman No. 1"
//            )
//            // Asumsi Anda punya OutletDao.insertOutlet(outlet)
//            // database.outletDao().insertOutlet(outlet)
//        }
//
//        /**
//         * Fungsi untuk menyuntikkan data produk dan varian uji.
//         * Mengembalikan beberapa ID penting untuk transaksi.
//         */
//        private suspend fun seedProducts(productDao: ProductDao, productItemDao: ProductItemDao): Triple<String, String, ProductItemEntity, ProductItemEntity> {
//            val productAyamGorengId = UUID.randomUUID().toString()
//            val productAyamBakarId = UUID.randomUUID().toString()
//
//            // 1. Produk Utama (ProductEntity)
//            val productAyamGoreng = ProductEntity(id = productAyamGorengId, name = "Ayam Goreng", price = 10000.0, stock = 999, isActive = true, isSynced = true)
//            val productAyamBakar = ProductEntity(id = productAyamBakarId, name = "Ayam Bakar", price = 10000.0, stock = 999, isActive = true, isSynced = true)
//            productDao.insertProducts(listOf(productAyamGoreng, productAyamBakar))
//
//            // 2. Varian Produk (ProductItemEntity)
//
//            // AYAM GORENG VARIAN
//            val ag_item1 = ProductItemEntity(id = UUID.randomUUID().toString(), productId = productAyamGorengId, name = "Ayam Goreng (Tanpa Nasi)", basePrice = 10000.0, sellingPrice = 10000.0, variantType = "TANPA_NASI")
//            val ag_item2 = ProductItemEntity(id = UUID.randomUUID().toString(), productId = productAyamGorengId, name = "Ayam Goreng (Nasi Biasa)", basePrice = 10000.0, sellingPrice = 13000.0, variantType = "NASI_BIASA") // +3000
//            val ag_item3 = ProductItemEntity(id = UUID.randomUUID().toString(), productId = productAyamGorengId, name = "Ayam Goreng (Nasi Double)", basePrice = 10000.0, sellingPrice = 15000.0, variantType = "NASI_DOUBLE") // +5000
//
//            // AYAM BAKAR VARIAN
//            val ab_item4 = ProductItemEntity(id = UUID.randomUUID().toString(), productId = productAyamBakarId, name = "Ayam Bakar (Tanpa Nasi)", basePrice = 10000.0, sellingPrice = 10000.0, variantType = "TANPA_NASI")
//            val ab_item5 = ProductItemEntity(id = UUID.randomUUID().toString(), productId = productAyamBakarId, name = "Ayam Bakar (Nasi Biasa)", basePrice = 10000.0, sellingPrice = 13000.0, variantType = "NASI_BIASA") // +3000
//            val ab_item6 = ProductItemEntity(id = UUID.randomUUID().toString(), productId = productAyamBakarId, name = "Ayam Bakar (Nasi Double)", basePrice = 10000.0, sellingPrice = 15000.0, variantType = "NASI_DOUBLE") // +5000
//
//            val allItems = listOf(ag_item1, ag_item2, ag_item3, ab_item4, ab_item5, ab_item6)
//
//            // Menggunakan insertProductItems (asumsi fungsi ini ada di ProductItemDao)
//            // productItemDao.insertProductItems(allItems) // Hapus jika sudah di implementasi sebelumnya
//            productItemDao.insertProductItem(ag_item1)
//            productItemDao.insertProductItem(ag_item2)
//            productItemDao.insertProductItem(ag_item3)
//            productItemDao.insertProductItem(ab_item4)
//            productItemDao.insertProductItem(ab_item5)
//            productItemDao.insertProductItem(ab_item6)
//
//
//            return Triple(productAyamGorengId, productAyamBakarId, ag_item2, ab_item5)
//        }
//
//        /**
//         * Fungsi untuk menyuntikkan data transaksi uji.
//         */
//        private suspend fun seedTransactions(
//            transactionDao: TransactionDao,
//            transactionItemDao: TransactionItemDao,
//            userId: String,
//            ag_itemId: String, // Ayam Goreng (Nasi Biasa)
//            ab_itemId: String  // Ayam Bakar (Nasi Biasa)
//        ) {
//            val txId = UUID.randomUUID().toString()
//            val totalAmount = 26000.0 // 13000 * 2
//
//            // 1. Transaction Entity
//            val transaction = TransactionEntity(
//                id = txId,
//                userId = userId,
//                outletId = OUTLET_ID,
//                totalAmount = totalAmount,
//                transactionTime = System.currentTimeMillis() - 3600000L, // 1 jam lalu
//                isSynced = false
//            )
//            transactionDao.insertTransaction(transaction)
//
//            // 2. Transaction Item Entities
//            val item1 = TransactionItemEntity(
//                transactionId = txId,
//                productItemId = ag_itemId,
//                quantity = 1,
//                itemPrice = 13000.0,
//                isSynced = false
//            )
//            val item2 = TransactionItemEntity(
//                transactionId = txId,
//                productItemId = ab_itemId,
//                quantity = 1,
//                itemPrice = 13000.0,
//                isSynced = false
//            )
//
//            // Menggunakan insertTransactionItems (asumsi fungsi ini ada di TransactionItemDao)
//            transactionItemDao.insertTransactionItems(listOf(item1, item2))
//        }
//
//
//        /**
//         * Fungsi untuk menyuntikkan data user uji.
//         */
//        private suspend fun seedUsers(userDao: UserDao) {
//            // Data Uji 1: Admin
//
//            val adminUser = UserEntity(
//                id = UUID.randomUUID().toString(),
//                name = "Admin Uji",
//                email = "admin@test.com",
//                password = passwordHelper.hashPassword("password123"), // Password TIDAK di-hash
//                role = "admin"
//            )
//
//            // Data Uji 2: Cashier
//            val cashierUser = UserEntity(
//                id = UUID.randomUUID().toString(),
//                name = "Kasir Toko",
//                email = "kasir@test.com",
//                password = passwordHelper.hashPassword("kasir123"), // Password TIDAK di-hash
//                role = "cashier"
//            )
//
//            userDao.insertUser(adminUser)
//            userDao.insertUser(cashierUser)
//
//            // Perhatian: Karena kita menggunakan UUID.randomUUID(), data baru akan dibuat
//            // setiap kali database dibuat ulang (misalnya setelah uninstall/clear data).
//        }
//    }
//
//}
