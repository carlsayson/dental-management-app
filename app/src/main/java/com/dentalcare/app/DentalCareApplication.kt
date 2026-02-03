package com.dentalcare.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DentalCareApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
