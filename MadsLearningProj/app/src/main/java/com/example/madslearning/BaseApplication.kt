package com.example.bitmapdemo

import android.app.Application
import android.content.Context
import com.facebook.drawee.backends.pipeline.Fresco

class BaseApplication: Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        CommonUtils.setBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        CommonUtils.setAppContext(this)
        Fresco.initialize(this)
    }
}