package com.turistgo.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

import com.cloudinary.android.MediaManager

@HiltAndroidApp
class TuristGoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            val config = mapOf(
                "cloud_name" to "doxdjiyvi",
                "api_key" to "681574593944424",
                "api_secret" to "ogCKETDiO1eIhLV4nfkqzRM_QoA"
            )
            MediaManager.init(this, config)
        } catch (e: Exception) {
            // Already initialized
        }
    }
}
