package com.praktikum.abstreetfood_management

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class ABStreetFoodManagement: Application() {


//    override fun onCreate() {
//        super.onCreate()
//
//        // Initialize Timber untuk logging
//        if (BuildConfig.DEBUG) {
//            Timber.plant(Timber.DebugTree())
//        }
//
//        Timber.d("StreetFoodApp initialized")
//    }
}