// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
//    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22" apply false
//    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21" apply false
    id("com.google.dagger.hilt.android") version "2.51" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.8.0" apply false
}