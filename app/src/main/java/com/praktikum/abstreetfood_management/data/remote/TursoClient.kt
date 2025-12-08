////package com.praktikum.abstreetfood_management.data.remote
////
////import io.ktor.client.HttpClient
////import io.ktor.client.call.body
////import io.ktor.client.engine.okhttp.OkHttp
////import io.ktor.client.request.*
////import io.ktor.client.statement.HttpResponse
////import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
////import io.ktor.client.plugins.HttpTimeout
////import io.ktor.http.ContentType
////import io.ktor.http.contentType
////import io.ktor.serialization.kotlinx.json.json
////import kotlinx.serialization.json.Json
////import com.praktikum.abstreetfood_management.data.local.entity.UserEntity
////import kotlinx.serialization.Serializable
////
////class TursoClient(private val apiKey: String, private val dbUrl: String) {
////
////    private val client = HttpClient(OkHttp) {
////        install(ContentNegotiation) {
////            json(Json {
////                ignoreUnknownKeys = true
////                isLenient = true
////                encodeDefaults = true
////            })
////        }
////        install(HttpTimeout) {
////            requestTimeoutMillis = 30000
////            connectTimeoutMillis = 30000
////            socketTimeoutMillis = 30000
////        }
////    }
////
////    // Login: Validasi credentials di server
////    suspend fun login(email: String, password: String): UserEntity? {
////        return try {
////            val response: HttpResponse = client.post("$dbUrl/auth/login") {
////                headers {
////                    append("Authorization", "Bearer $apiKey")
////                }
////                contentType(ContentType.Application.Json)
////                setBody(LoginRequest(email, password))
////            }
////            response.body()
////        } catch (e: Exception) {
////            e.printStackTrace()
////            null
////        }
////    }
////
////    // Get all users untuk sync
////    suspend fun getUsers(): List<UserEntity> {
////        return try {
////            val response: HttpResponse = client.get("$dbUrl/users") {
////                headers {
////                    append("Authorization", "Bearer $apiKey")
////                }
////            }
////            response.body()
////        } catch (e: Exception) {
////            e.printStackTrace()
////            emptyList()
////        }
////    }
////
////    // Get user by email
////    suspend fun getUserByEmail(email: String): UserEntity? {
////        return try {
////            val response: HttpResponse = client.get("$dbUrl/users/email/$email") {
////                headers {
////                    append("Authorization", "Bearer $apiKey")
////                }
////            }
////            response.body()
////        } catch (e: Exception) {
////            e.printStackTrace()
////            null
////        }
////    }
////
////    // Register new user
////    suspend fun register(user: UserEntity): UserEntity? {
////        return try {
////            val response: HttpResponse = client.post("$dbUrl/users") {
////                headers {
////                    append("Authorization", "Bearer $apiKey")
////                }
////                contentType(ContentType.Application.Json)
////                setBody(user)
////            }
////            response.body()
////        } catch (e: Exception) {
////            e.printStackTrace()
////            null
////        }
////    }
////
////    fun close() {
////        client.close()
////    }
////}
////
////@Serializable
////data class LoginRequest(
////    val email: String,
////    val password: String
////)
//
//package com.praktikum.abstreetfood_management.data.remote
//
//import android.util.Log
//import io.ktor.client.HttpClient
//import io.ktor.client.call.body
//import io.ktor.client.engine.okhttp.OkHttp
//import io.ktor.client.request.*
//import io.ktor.client.statement.HttpResponse
//import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
//import io.ktor.client.plugins.HttpTimeout
//import io.ktor.http.ContentType
//import io.ktor.http.contentType
//import io.ktor.serialization.gson.gson
//import com.praktikum.abstreetfood_management.data.local.entity.UserEntity
//import com.praktikum.abstreetfood_management.data.remote.response.LoginResponse
//import com.praktikum.abstreetfood_management.data.remote.response.UserDto
//import com.praktikum.abstreetfood_management.domain.LoginRequest
//import io.ktor.client.statement.bodyAsText
//
//class TursoClient(private val apiKey: String, private val dbUrl: String) {
//
//    private val client = HttpClient(OkHttp) {
//        install(ContentNegotiation) {
//            gson {
//                // Konfigurasi Gson
//                setPrettyPrinting()
//                setLenient()
//                serializeNulls()
//            }
//        }
//        install(HttpTimeout) {
//            requestTimeoutMillis = 30000
//            connectTimeoutMillis = 30000
//            socketTimeoutMillis = 30000
//        }
//    }
//
//    // Login: Validasi credentials di server
//    suspend fun login(email: String, password: String): UserDto? { // <<< UBAH RETURN TYPE
//        return try {
//            val response: HttpResponse = client.post("$dbUrl/auth/login") {
//                headers {
//                    append("Authorization", "Bearer $apiKey")
//                }
//                contentType(ContentType.Application.Json)
//                setBody(LoginRequest(email, password))
//            }
//
//            if (response.status.value != 200) {
//                val responseBody = response.bodyAsText()
//                Log.e("TURSO_CLIENT", "❌ LOGIN GAGAL! Status HTTP: ${response.status.value}")
//                Log.e("TURSO_CLIENT", "Respons Body dari Turso: $responseBody")
//                return null // Kredensial salah atau API menolak
//            }
//
//            // Jika status 200 OK, parse ke LoginResponse
//            val loginResponse = response.body<LoginResponse>() // <<< PARSE KE LOGINRESPONSE
//            Log.i("TURSO_CLIENT", "✅ Login Sukses. Data User Diterima.")
//
//            // Kembalikan objek UserDto yang ada di dalam LoginResponse
//            return loginResponse.user
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Log.e("TURSO_CLIENT", "💥 EXCEPTION JARINGAN/PARSING: ${e.message}")
//            null
//        }
//    }
//
//    // Get all users untuk sync
//    suspend fun getUsers(): List<UserEntity> {
//        return try {
//            val response: HttpResponse = client.get("$dbUrl/users") {
//                headers {
//                    append("Authorization", "Bearer $apiKey")
//                }
//            }
//            response.body()
//        } catch (e: Exception) {
//            e.printStackTrace()
//            emptyList()
//        }
//    }
//
//    // Get user by email
//    suspend fun getUserByEmail(email: String): UserEntity? {
//        return try {
//            val response: HttpResponse = client.get("$dbUrl/users/email/$email") {
//                headers {
//                    append("Authorization", "Bearer $apiKey")
//                }
//            }
//            response.body()
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }
//
//    // Register new user
//    suspend fun register(user: UserEntity): UserEntity? {
//        return try {
//            val response: HttpResponse = client.post("$dbUrl/users") {
//                headers {
//                    append("Authorization", "Bearer $apiKey")
//                }
//                contentType(ContentType.Application.Json)
//                setBody(user)
//            }
//            response.body()
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }
//
//    fun close() {
//        client.close()
//    }
//}
//
