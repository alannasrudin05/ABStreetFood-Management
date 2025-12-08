package com.praktikum.abstreetfood_management.data.repository

import com.praktikum.abstreetfood_management.domain.model.Product
import com.praktikum.abstreetfood_management.domain.model.ProductItem
import com.praktikum.abstreetfood_management.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

interface IProductRepository {
    // CRUD Produk Master (Ayam Bakar)
    suspend fun insertProduct(product: Product)
    fun getAllProducts(): Flow<List<Product>>
    suspend fun getProductById(productId: String): Product?
    fun getAllProductItems(): Flow<List<ProductItem>>
    // CRUD Varian Produk (Dada Nasi Biasa)
    suspend fun insertProductItem(productItem: ProductItem)
    fun getProductItemsByProductId(productId: String): Flow<List<ProductItem>>
    suspend fun getProductItemById(productItemId: String): ProductItem?

    // CRUD Resep (BOM)
    suspend fun insertRecipe(recipe: Recipe)
    suspend fun getRecipeByProductItemId(productItemId: String): List<Recipe>

    // Sinkronisasi
    suspend fun syncProductsAndRecipes()
}