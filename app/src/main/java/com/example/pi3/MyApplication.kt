package com.example.pi3

import android.app.Application
import com.cloudinary.android.MediaManager

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val config: HashMap<String, String> = hashMapOf(
            "cloud_name" to "dvbsnzz2w",
            "api_key" to "481319899281888",
            "api_secret" to "kq6POtYa777t6ypxf8z0hIC9m80",
            "secure" to "true"
        )
        MediaManager.init(this, config)
    }
}
