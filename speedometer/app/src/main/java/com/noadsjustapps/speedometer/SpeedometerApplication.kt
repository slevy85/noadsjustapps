package com.noadsjustapps.speedometer

import android.app.Application
import org.osmdroid.config.Configuration

class SpeedometerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        Configuration.getInstance().apply {
            userAgentValue = packageName
            osmdroidBasePath = cacheDir
            osmdroidTileCache = cacheDir.resolve("osmdroid/tiles")
        }
    }
}
