package com.example.legacyframeapp

import android.app.Application
import com.example.legacyframeapp.data.AppContainer

class LegacyFrameApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
