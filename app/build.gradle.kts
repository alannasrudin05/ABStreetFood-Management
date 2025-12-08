plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.dagger.hilt.android")
    id("androidx.navigation.safeargs.kotlin")
    alias(libs.plugins.kotlin.kapt)
    id("kotlin-parcelize")
//    alias(libs.plugins.kotlin.serialization)
//    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"
}

android {
    namespace = "com.praktikum.abstreetfood_management"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.praktikum.abstreetfood_management"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

// Navigation Component
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.2")
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.2")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.2")

    // Room (local database)
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // Turso (remote database)
//    implementation("com.turso:turso-android:1.0.0")
    // GUNAKAN BARIS YANG BENAR INI
//    implementation("com.github.chiselstrike:turso-android-sdk:1.0.0")
//    implementation("tech.turso:turso:0.0.1-SNAPSHOT")
    implementation("tech.turso.libsql:libsql:0.1.0")

    // Networking (jika butuh REST API fallback)
    implementation("io.ktor:ktor-client-okhttp:2.3.6")
//    implementation("io.ktor:ktor-client-serialization:2.3.6")
    implementation("io.ktor:ktor-client-core:2.3.6")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.6")

    // Kotlinx Serialization - WAJIB untuk JSON parsing
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation("io.ktor:ktor-serialization-gson:2.3.6") // Pakai Gson

    // Gson untuk JSON parsing
    implementation("com.google.code.gson:gson:2.10.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    // Logging (optional, untuk debugging sinkronisasi)
    implementation("com.jakewharton.timber:timber:5.0.1")

    // 🔹 Hilt
    implementation("com.google.dagger:hilt-android:2.51")
    kapt("com.google.dagger:hilt-compiler:2.51")

    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // DataStore Preferences
    implementation ("androidx.datastore:datastore-preferences:1.1.1")

    // WorkManager
    val work_version = "2.9.0"
    implementation("androidx.work:work-runtime-ktx:$work_version")
    implementation("androidx.hilt:hilt-work:1.2.0")

    // --- RETROFIT & CONVERTER ---
    // 1. Retrofit Core Library
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // 2. Gson Converter (untuk parsing JSON)
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // 3. OkHttp Logging Interceptor (Opsional, tapi sangat direkomendasikan untuk debugging)
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    implementation("io.coil-kt:coil:2.6.0")

    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

}