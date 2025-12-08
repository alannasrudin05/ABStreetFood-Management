package com.praktikum.abstreetfood_management.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.praktikum.abstreetfood_management.data.local.entity.ProductItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductItemDao {

    // CREATE / UPDATE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductItem(item: ProductItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductItems(items: List<ProductItemEntity>)

    // READ
    @Query("SELECT * FROM product_item")
    fun getAllProductItems(): Flow<List<ProductItemEntity>>

    @Query("SELECT * FROM product_item WHERE id = :id")
    suspend fun getProductItemById(id: String): ProductItemEntity?

    @Query("SELECT * FROM product_item WHERE productId = :productId ORDER BY sellingPrice ASC")
    fun getProductItemsByProductId(productId: String): Flow<List<ProductItemEntity>>

    // DELETE
    @Query("DELETE FROM product_item WHERE id = :id")
    suspend fun deleteProductItem(id: String)
}