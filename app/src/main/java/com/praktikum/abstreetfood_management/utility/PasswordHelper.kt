package com.praktikum.abstreetfood_management.utility

import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.joinToString
import kotlin.text.format
import kotlin.text.toByteArray

@Singleton
class PasswordHelper @Inject constructor() { // ✅ Dibuat sebagai kelas yang bisa diinjeksi

    private val HASH_ALGORITHM = "SHA-256"

    /**
     * Menghitung hash (SHA-256) dari password plain text.
     */
    fun hashPassword(password: String): String {
        return try {
            val digest = MessageDigest.getInstance(HASH_ALGORITHM)
            val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
            hashBytes.toHex()
        } catch (e: Exception) {
            throw RuntimeException("Failed to hash password", e)
        }
    }

    /**
     * Memverifikasi password plain text dengan hash yang tersimpan.
     */
    fun verifyPassword(password: String, storedHash: String): Boolean {
        // Cukup hash password yang dimasukkan dan bandingkan
        val inputHash = hashPassword(password)
        return inputHash == storedHash
    }

    private fun ByteArray.toHex(): String =
        joinToString("") { "%02x".format(it) }


}


//package com.praktikum.vintora.utils
//
//import java.security.MessageDigest
//import javax.inject.Inject
//import javax.inject.Singleton
//
//@Singleton
//class PasswordHelper @Inject constructor() {
//
//    companion object {
//        // --- Konstanta ---
//        private val HASH_ALGORITHM = "SHA-256"
//
//        /**
//         * Menghitung hash (SHA-256) dari password plain text.
//         * Untuk aplikasi produksi, sangat disarankan menggunakan fungsi hashing yang lebih kuat
//         * seperti BCrypt atau Argon2 yang sudah memiliki built-in salt dan work factor.
//         */
//        fun hashPassword(password: String): String {
//            return try {
//                val digest = MessageDigest.getInstance(HASH_ALGORITHM)
//                val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
//
//                // Konversi byte array ke heksadesimal string
//                hashBytes.toHex()
//            } catch (e: Exception) {
//                // Dalam kasus error, biasanya kita log dan melempar RuntimeException.
//                throw RuntimeException("Failed to hash password", e)
//            }
//        }
//
//        /**
//         * Memverifikasi password plain text dengan hash yang tersimpan.
//         */
//        fun verifyPassword(password: String, storedHash: String): Boolean {
//            // Cukup hash password yang dimasukkan dan bandingkan dengan hash yang tersimpan
//            val inputHash = hashPassword(password)
//            return inputHash == storedHash
//        }
//
//        // --- Fungsi Helper untuk konversi Byte Array ke String Hex ---
//        private fun ByteArray.toHex(): String =
//            joinToString("") { "%02x".format(it) }
//
//    }
//}