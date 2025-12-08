//package com.praktikum.abstreetfood_management.domain.usecase
//
//import com.praktikum.abstreetfood_management.domain.model.Product
//import com.praktikum.abstreetfood_management.domain.model.ProductItem
//import com.praktikum.abstreetfood_management.domain.model.Recipe
//import com.praktikum.abstreetfood_management.domain.model.User
//import com.praktikum.abstreetfood_management.data.repository.IUserRepository
//import com.praktikum.abstreetfood_management.data.repository.IProductRepository
//import com.praktikum.abstreetfood_management.data.repository.IStockRepository
//import com.praktikum.abstreetfood_management.utility.Resource
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.flow
//import timber.log.Timber
//import java.util.UUID
//import javax.inject.Inject
//
///**
// * AdminUseCase: Menangani semua logika bisnis yang membutuhkan otorisasi tinggi,
// * seperti manajemen menu, resep, dan peran pengguna.
// *
// * Catatan: Asumsikan Anda telah membuat interface IProductRepository dan IStockRepository
// * yang menangani konversi Entity ke Domain dan integrasi Room/Turso.
// */
//class AdminUseCase @Inject constructor(
//    private val userRepository: IUserRepository,
//    private val productRepository: IProductRepository,
//    private val stockRepository: IStockRepository
//) {
//
//    // =========================================================================
//    // 1. MANAJEMEN PRODUK (MENU) & VARIAN
//    // =========================================================================
//
//    /**
//     * Menambahkan produk master baru (misalnya: 'Ayam Bakar').
//     */
//    fun addProduct(name: String, basePrice: Double): Flow<Resource<Product>> = flow {
//        emit(Resource.Loading())
//        try {
//            val newProduct = Product(
//                id = UUID.randomUUID().toString(),
//                name = name,
//                price = basePrice,
//                stock = 0, // Stok awal selalu 0
//                isActive = true
//            )
//
//            productRepository.insertProduct(newProduct)
//            emit(Resource.Success(newProduct))
//            Timber.i("Admin: Berhasil menambahkan produk master: $name")
//        } catch (e: Exception) {
//            Timber.e(e, "Admin: Gagal menambahkan produk.")
//            emit(Resource.Error("Gagal menambahkan produk: ${e.message}"))
//        }
//    }
//
//    /**
//     * Menambahkan varian produk (misalnya: 'Dada Nasi Biasa').
//     */
//    fun addProductVariant(
//        productId: String,
//        variantName: String,
//        sellingPrice: Double,
//        variantType: String
//    ): Flow<Resource<ProductItem>> = flow {
//        emit(Resource.Loading())
//        try {
//            val newVariant = ProductItem(
//                id = UUID.randomUUID().toString(),
//                productId = productId,
//                name = variantName,
//                sellingPrice = sellingPrice,
//                variantType = variantType
//            )
//
//            productRepository.insertProductItem(newVariant)
//            emit(Resource.Success(newVariant))
//            Timber.i("Admin: Berhasil menambahkan varian: $variantName")
//        } catch (e: Exception) {
//            Timber.e(e, "Admin: Gagal menambahkan varian produk.")
//            emit(Resource.Error("Gagal menambahkan varian produk: ${e.message}"))
//        }
//    }
//
//    // =========================================================================
//    // 2. MANAJEMEN RESEP (BILL OF MATERIALS / BOM)
//    // =========================================================================
//
//    /**
//     * Menambahkan resep (bahan baku yang dibutuhkan) untuk satu varian produk.
//     * @param productItemId ID dari varian produk (misal: Ayam Bakar Dada Nasi Biasa)
//     * @param stockItemId ID dari bahan baku (misal: Potongan Ayam Dada)
//     * @param quantityNeeded Jumlah bahan yang dibutuhkan per unit produk.
//     */
//    fun addRecipe(
//        productItemId: String,
//        stockItemId: String,
//        quantityNeeded: Int
//    ): Flow<Resource<Recipe>> = flow {
//        emit(Resource.Loading())
//        try {
//            // Logika validasi: Pastikan productItemId dan stockItemId ada.
//            if (productRepository.getProductItemById(productItemId) == null) {
//                emit(Resource.Error("Varian Produk tidak ditemukan."))
//                return@flow
//            }
//            if (stockRepository.getStockItemById(stockItemId) == null) {
//                emit(Resource.Error("Bahan Baku tidak ditemukan."))
//                return@flow
//            }
//
//            val newRecipe = Recipe(
//                id = UUID.randomUUID().toString(),
//                productItemId = productItemId,
//                stockItemId = stockItemId,
//                requireQuantity = quantityNeeded
//            )
//
//            productRepository.insertRecipe(newRecipe)
//            emit(Resource.Success(newRecipe))
//            Timber.i("Admin: Berhasil menambahkan resep untuk $productItemId")
//        } catch (e: Exception) {
//            Timber.e(e, "Admin: Gagal menambahkan resep.")
//            emit(Resource.Error("Gagal menambahkan resep: ${e.message}"))
//        }
//    }
//
//    // =========================================================================
//    // 3. MANAJEMEN PENGGUNA (ROLE & STATUS)
//    // =========================================================================
//
//    /**
//     * Mengubah peran (role) pengguna.
//     * @param userId ID pengguna yang akan diubah.
//     * @param newRole Peran baru ('admin', 'owner', 'cashier').
//     */
//    fun updateUserRole(userId: String, newRole: String): Flow<Resource<User>> = flow {
//        emit(Resource.Loading())
//        try {
//            val userToUpdate = userRepository.getUserById(userId)
//            if (userToUpdate == null) {
//                emit(Resource.Error("Pengguna tidak ditemukan."))
//                return@flow
//            }
//
//            // Pastikan role valid
//            if (newRole !in listOf("admin", "owner", "cashier")) {
//                emit(Resource.Error("Peran yang dimasukkan tidak valid."))
//                return@flow
//            }
//
//            val updatedUser = userToUpdate.copy(role = newRole)
//            userRepository.saveUserLocal(updatedUser) // Simpan lokal
//
//            // TODO: Tambahkan panggilan ke userRepository.updateUserRemote(updatedUser)
//            // untuk push perubahan ke Turso segera.
//
//            emit(Resource.Success(updatedUser))
//            Timber.i("Admin: Berhasil mengubah peran user $userId menjadi $newRole")
//        } catch (e: Exception) {
//            Timber.e(e, "Admin: Gagal mengubah peran pengguna.")
//            emit(Resource.Error("Gagal mengubah peran: ${e.message}"))
//        }
//    }
//
//    /**
//     * Mengaktifkan atau menonaktifkan akun pengguna.
//     */
//    fun setUserActiveStatus(userId: String, isActive: Boolean): Flow<Resource<User>> = flow {
//        emit(Resource.Loading())
//        try {
//            val userToUpdate = userRepository.getUserById(userId)
//            if (userToUpdate == null) {
//                emit(Resource.Error("Pengguna tidak ditemukan."))
//                return@flow
//            }
//
//            val updatedUser = userToUpdate.copy(isActive = isActive)
//            userRepository.saveUserLocal(updatedUser) // Simpan lokal
//
//            // TODO: Tambahkan panggilan ke userRepository.updateUserRemote(updatedUser)
//
//            emit(Resource.Success(updatedUser))
//            Timber.i("Admin: Status user $userId diubah menjadi Aktif: $isActive")
//        } catch (e: Exception) {
//            Timber.e(e, "Admin: Gagal mengubah status aktif pengguna.")
//            emit(Resource.Error("Gagal mengubah status aktif: ${e.message}"))
//        }
//    }
//
//    /**
//     * Mendapatkan daftar semua pengguna (untuk tampilan manajemen admin).
//     */
//    fun getAllUsers(): Flow<Resource<List<User>>> = flow {
//        emit(Resource.Loading())
//        try {
//            // Ambil semua user dari repository (yang akan memprioritaskan sinkronisasi)
//            val users = userRepository.getUsers(forceRefresh = false)
//            emit(Resource.Success(users))
//            Timber.d("Admin: Memuat ${users.size} pengguna untuk manajemen.")
//        } catch (e: Exception) {
//            Timber.e(e, "Admin: Gagal memuat daftar pengguna.")
//            emit(Resource.Error("Gagal memuat daftar pengguna: ${e.message}"))
//        }
//    }
//
//}