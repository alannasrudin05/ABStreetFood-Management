package com.praktikum.abstreetfood_management.data.repository

import android.annotation.SuppressLint
import com.praktikum.abstreetfood_management.data.local.dao.ProductDao // <<< BARU
import com.praktikum.abstreetfood_management.data.local.dao.ProductItemDao
import com.praktikum.abstreetfood_management.data.local.dao.RecipeDao
import com.praktikum.abstreetfood_management.data.mapper.toDomain
import com.praktikum.abstreetfood_management.data.mapper.toEntity
import com.praktikum.abstreetfood_management.domain.model.Product
import com.praktikum.abstreetfood_management.domain.model.ProductItem
import com.praktikum.abstreetfood_management.domain.model.Recipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    // INJEKSI DAO SECARA LANGSUNG DARI HILT
    private val productDao: ProductDao,
    private val productItemDao: ProductItemDao,
    private val recipeDao: RecipeDao,
) : IProductRepository {

    // DAOs sekarang langsung tersedia sebagai field, tanpa perlu inisialisasi tambahan

    // --- CRUD Produk Master ---

    override suspend fun insertProduct(product: Product) {
        productDao.insertProduct(product.toEntity())
        Timber.d("Product inserted locally: ${product.name}")
    }

    override fun getAllProducts(): Flow<List<Product>> {
        return productDao.getAllProducts().map { list ->
            list.map { it.toDomain() }
        }
    }
//    override fun getAllProductItems(): Flow<List<ProductItem>> {
//        // Asumsi ProductItemDao memiliki getAllProductItems() yang mengembalikan Flow<List<ProductItemEntity>>
//        return productItemDao.getAllProductItems().map { list ->
//            list.map { it.toDomain() } // Menggunakan mapper ProductItemEntity -> ProductItem
//        }
//    }
@SuppressLint("TimberArgCount")
override fun getAllProductItems(): Flow<List<ProductItem>> {
    return productItemDao.getAllProductItems().map { list ->
        // --- LOGGING BARU: Periksa Entitas ---
        Timber.d("PRODUCT_REPO_LOAD", "Loaded ${list.size} ProductItem Entities from DB.")

        val domainList = list.map { entity ->
            // --- LOGGING BARU: Periksa Domain Model ---
            Timber.d("PRODUCT_REPO_LOAD",
                "Mapping Entity: ID=${entity.id}, Name=${entity.name}, Price=${entity.sellingPrice}, Variant=${entity.variantType}"
            )
            entity.toDomain() // Menggunakan mapper ProductItemEntity -> ProductItem
        }

        return@map domainList
    }
}

    override suspend fun getProductById(productId: String): Product? {
        return productDao.getProductById(productId)?.toDomain() // Asumsi fungsi ini ada di ProductDao
    }

    // --- CRUD Varian Produk (Menggunakan ProductItemDao) ---

    override suspend fun insertProductItem(productItem: ProductItem) {
        productItemDao.insertProductItem(productItem.toEntity())
        Timber.d("Product item inserted locally: ${productItem.name}")
    }

    override fun getProductItemsByProductId(productId: String): Flow<List<ProductItem>> {
        return productItemDao.getProductItemsByProductId(productId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getProductItemById(productItemId: String): ProductItem? {
        // Asumsi ProductItemDao memiliki getProductItemById(id: String)
        return productItemDao.getProductItemById(productItemId)?.toDomain()
    }

    // --- CRUD Resep (Menggunakan RecipeDao) ---

    override suspend fun insertRecipe(recipe: Recipe) {
        recipeDao.insertRecipe(recipe.toEntity())
        Timber.d("Recipe inserted locally for: ${recipe.productItemId}")
    }

    override suspend fun getRecipeByProductItemId(productItemId: String): List<Recipe> {
        return recipeDao.getRecipeByProductItemId(productItemId).map { it.toDomain() }
    }

    // --- Sinkronisasi ---

    override suspend fun syncProductsAndRecipes() {
        Timber.i("Starting product and recipe background sync...")
        // ... (Logika sinkronisasi)
        Timber.i("Product and recipe sync finished.")
    }
}