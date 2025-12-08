package com.praktikum.abstreetfood_management.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.praktikum.abstreetfood_management.data.local.entity.ProductEntity
import com.praktikum.abstreetfood_management.domain.model.TopSellingProduct
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE isActive = 1")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: String): ProductEntity?
//    // [BARU] Query untuk mendapatkan produk terlaris (Hanya Konsep)
//    @Query("""
//        SELECT p.name, SUM(t.totalAmount) as totalRevenue
//        FROM transactions t
//        JOIN products p ON t.productId = p.id  -- (Asumsi kolom productId ditambahkan ke transactions)
//        GROUP BY p.name
//        ORDER BY totalRevenue DESC
//        LIMIT 5
//    """)
//    fun getTopSellingProducts(): Flow<List<TopSellingProduct>> // <<< Membutuhkan data class baru

    // [BARU] Query untuk mendapatkan total stok (Hanya Konsep)
//    @Query("SELECT SUM(stock) FROM products")
//    fun getTotalStock(): Flow<Int?>

}