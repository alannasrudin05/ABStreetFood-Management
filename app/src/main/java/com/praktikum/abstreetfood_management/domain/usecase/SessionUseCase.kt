package com.praktikum.abstreetfood_management.domain.usecase

import com.praktikum.abstreetfood_management.data.repository.IAuthPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// File: SessionUseCase.kt (NEW FILE)
class SessionUseCase @Inject constructor(
    private val authPrefs: IAuthPreferences // Gunakan IAuthPreferences sebagai dependency
) {
    // Mengambil User ID saat ini dalam bentuk Flow
    fun getCurrentUserId(): Flow<String?> {
        return authPrefs.getUserId()
    }
    // Mengambil User Role saat ini dalam bentuk Flow
    fun getCurrentUserRole(): Flow<String?> {
        return authPrefs.getUserRole()
    }
    fun getCurrentUserName(): Flow<String?> {
        return authPrefs.getUserName()
    }
}