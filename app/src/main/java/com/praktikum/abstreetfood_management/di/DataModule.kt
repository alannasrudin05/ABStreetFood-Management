package com.praktikum.abstreetfood_management.di

import com.praktikum.abstreetfood_management.data.repository.AuthPreferencesImpl
import com.praktikum.abstreetfood_management.data.repository.IAuthPreferences
import com.praktikum.abstreetfood_management.data.repository.IUserRepository
import com.praktikum.abstreetfood_management.data.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    /**
     * Mengikat implementasi konkret (UserRepository) ke interface domain (IUserRepository).
     * Hilt akan memberikan UserRepository setiap kali IUserRepository diminta.
     */
//    @Binds
//    @Singleton
//    abstract fun bindUserRepository(
//        userRepository: UserRepository // Parameter type harus mengimplementasikan return type
//    ): IUserRepository

    // ... (Bind Repository lainnya di sini)

    @Binds // <-- Hilt akan menggunakan AuthPreferencesImpl saat IAuthPreferences diminta
    @Singleton
    abstract fun bindAuthPreferences(
        authPreferencesImpl: AuthPreferencesImpl
    ): IAuthPreferences
}