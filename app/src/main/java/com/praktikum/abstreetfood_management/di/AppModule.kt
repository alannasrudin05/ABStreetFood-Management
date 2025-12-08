package com.praktikum.abstreetfood_management.di

import android.content.Context
import com.praktikum.abstreetfood_management.data.local.AppDatabase
import com.praktikum.abstreetfood_management.data.local.dao.OutletDao
import com.praktikum.abstreetfood_management.data.local.dao.ProductDao
import com.praktikum.abstreetfood_management.data.local.dao.ProductItemDao
import com.praktikum.abstreetfood_management.data.local.dao.RecipeDao
import com.praktikum.abstreetfood_management.data.local.dao.ShiftDao
import com.praktikum.abstreetfood_management.data.local.dao.StockItemDao
import com.praktikum.abstreetfood_management.data.local.dao.TransactionDao
import com.praktikum.abstreetfood_management.data.local.dao.TransactionItemDao
import com.praktikum.abstreetfood_management.data.local.dao.UserDao
import com.praktikum.abstreetfood_management.data.network.ApiService
import com.praktikum.abstreetfood_management.data.remote.RetrofitClient
//import com.praktikum.abstreetfood_management.data.repository.AuthRepository
import com.praktikum.abstreetfood_management.data.repository.UserRepository
import com.praktikum.abstreetfood_management.data.repository.IUserRepository
import com.praktikum.abstreetfood_management.data.repository.DashboardRepository
import com.praktikum.abstreetfood_management.data.repository.IAuthPreferences
import com.praktikum.abstreetfood_management.data.repository.IProductRepository
import com.praktikum.abstreetfood_management.data.repository.ITransactionRepository
import com.praktikum.abstreetfood_management.data.repository.ProductRepository
import com.praktikum.abstreetfood_management.data.repository.TransactionRepository
import com.praktikum.abstreetfood_management.domain.usecase.AuthUseCase
import com.praktikum.abstreetfood_management.utility.PasswordHelper
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideApplicationContext(@ApplicationContext context: Context): Context {
        return context
    }

    // ========== DATABASE ==========

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    // --- DAO PROVIDERS ---
    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideTransactionDao(database: AppDatabase): TransactionDao {
        return database.transactionDao()
    }

    @Provides
    @Singleton
    fun provideTransactionItemDao(database: AppDatabase): TransactionItemDao {
        return database.transactionItemDao()
    }

    @Provides
    @Singleton
    fun provideProductDao(database: AppDatabase): ProductDao {
        return database.productDao()
    }

    @Provides
    @Singleton
    fun provideStockItemDao(database: AppDatabase): StockItemDao {
        return database.stockItemDao()
    }

    @Provides
    @Singleton
    fun provideShiftDao(database: AppDatabase): ShiftDao {
        return database.shiftDao()
    }

    @Provides
    @Singleton
    fun provideProductItemDao(appDatabase: AppDatabase): ProductItemDao {
        // Room secara otomatis menghasilkan fungsi ini di AppDatabase
        return appDatabase.productItemDao()
    }

    @Provides
    @Singleton
    fun provideRecipeDao(appDatabase: AppDatabase): RecipeDao {
        // Room secara otomatis menghasilkan fungsi ini di AppDatabase
        return appDatabase.recipeDao()
    }
    @Provides
    @Singleton
    fun provideOutletDao(database: AppDatabase): OutletDao {
        // Asumsi AppDatabase.kt memiliki 'abstract fun outletDao(): OutletDao'
        return database.outletDao()
    }

//    @Provides
//    @Singleton
//    fun provideOutletDao(database: AppDatabase): OutletDao {
//        // Asumsi Anda punya OutletDao
//        // return database.outletDao()
//        // Karena OutletDao tidak disediakan, ini harus ditambahkan di AppDatabase
//        throw NotImplementedError("OutletDao belum disediakan atau diinisialisasi.")
//    }

    // ========== REMOTE ==========


    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        // Memanggil object RetrofitClient yang sudah Anda buat
        return RetrofitClient.apiService
    }

    // ========== REPOSITORIES ==========

    @Provides
    @Singleton
    fun provideUserRepository(
        appDatabase: AppDatabase,
        apiService: ApiService, // <-- BARU
        authPrefs: IAuthPreferences,
        passwordHelper: PasswordHelper// <-- BARU
    ): IUserRepository {
        // PASTIKAN CONSTRUCTOR UserRepository menerima IAuthPreferences sebagai argumen ke-3
        return UserRepository(appDatabase, apiService, authPrefs, passwordHelper)
    }

    @Provides
    @Singleton
    fun provideDashboardRepository(
        transactionDao: TransactionDao,
        productDao: ProductDao,
        transactionItemDao: TransactionItemDao,
        stockItemDao: StockItemDao
    ): DashboardRepository {
        return DashboardRepository(transactionDao, productDao, transactionItemDao, stockItemDao)
    }

    // AuthRepository
//    @Provides
//    @Singleton
//    fun provideAuthRepository(
//        userDao: UserDao,
//        tursoService: TursoService
//    ): IAuthRepository {
//        return AuthRepository(userDao, tursoService)
//    }

    // TransactionRepository
    @Provides
    @Singleton
    fun provideTransactionRepository(
        transactionDao: TransactionDao,
        transactionItemDao: TransactionItemDao,
        shiftDao: ShiftDao,
        apiService: ApiService
    ): ITransactionRepository {
        return TransactionRepository(transactionDao, transactionItemDao, shiftDao, apiService)
    }

    // ProductRepository
    @Provides
    @Singleton
    fun provideProductRepository(
        productDao: ProductDao,
        productItemDao: ProductItemDao,
        recipeDao: RecipeDao
    ): IProductRepository {
        return ProductRepository(productDao, productItemDao, recipeDao)
    }



//    @Provides
//    @Singleton
//    fun provideTransactionDao(database: AppDatabase): TransactionDao {
//        return database.transactionDao()
//    }
//
//    @Provides
//    @Singleton
//    fun provideTransactionItemDao(database: AppDatabase): TransactionItemDao {
//        return database.transactionItemDao()
//    }

    // ========== REPOSITORIES ==========
//
//    @Provides
//    @Singleton
//    fun provideAuthRepository(
//        database: AppDatabase,
//        tursoClient: TursoClient
//    ): AuthRepository {
//        return AuthRepository(database, tursoClient)
//    }

//    @Provides
//    @Singleton
//    fun provideUserRepository(
//        database: AppDatabase,
//        tursoService: TursoService
//    ): IUserRepository {
//        return UserRepository(database, tursoService)
//    }

    // ========== USE CASES ==========

    @Provides
    fun provideAuthUseCase(userRepository: IUserRepository, passwordHelper: PasswordHelper): AuthUseCase {
        return AuthUseCase(userRepository, passwordHelper)
    }


}