// data/local/dao/RecipeDao.kt (BARU)
package com.praktikum.abstreetfood_management.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.praktikum.abstreetfood_management.data.local.entity.RecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity)

    // Kunci untuk Algoritma RIRA (Mendapatkan semua bahan baku yang dibutuhkan oleh 1 varian produk)
    @Query("SELECT * FROM recipe WHERE productItemId = :productItemId")
    suspend fun getRecipeByProductItemId(productItemId: String): List<RecipeEntity>
}