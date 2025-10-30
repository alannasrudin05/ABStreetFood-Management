package com.praktikum.abstreetfood_management.di

import android.content.Context
import com.praktikum.abstreetfood_management.data.local.AppDatabase
import com.praktikum.abstreetfood_management.data.local.dao.UserDao
import com.praktikum.abstreetfood_management.data.remote.TursoClient
//import com.praktikum.abstreetfood_management.data.repository.AuthRepository
import com.praktikum.abstreetfood_management.data.repository.UserRepository
import com.praktikum.abstreetfood_management.data.repository.IUserRepository
import com.praktikum.abstreetfood_management.data.remote.TursoService
import com.praktikum.abstreetfood_management.domain.usecase.AuthUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ========== DATABASE ==========

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    // ========== REMOTE ==========

    @Provides
    @Singleton
    fun provideTursoClient(): TursoClient {
        // TODO: Ganti dengan credentials Anda dari local.properties atau BuildConfig
        return TursoClient(
            apiKey = "YOUR_TURSO_AUTH_TOKEN", // Ganti dengan token Anda
            dbUrl = "https://YOUR_DB_URL.turso.io" // Ganti dengan URL Anda
        )
    }

    @Provides
    @Singleton
    fun provideTursoService(tursoClient: TursoClient): TursoService {
        return TursoService(tursoClient)
    }

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

    @Provides
    @Singleton
    fun provideUserRepository(
        database: AppDatabase,
        tursoService: TursoService
    ): IUserRepository {
        return UserRepository(database, tursoService)
    }

    // ========== USE CASES ==========

    @Provides
    fun provideAuthUseCase(userRepository: IUserRepository): AuthUseCase {
        return AuthUseCase(userRepository)
    }


}