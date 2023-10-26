package com.example.bitmapdemo

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.util.DisplayMetrics

@SuppressLint("StaticFieldLeak")
object CommonUtils {
    private var sBaseContext: Context? = null
    private lateinit var sAppContext: Context
    fun setBaseContext(context: Context) {
        sBaseContext = context
    }
    fun setAppContext(context: Context) {
        sAppContext = context
    }

    fun getContext(): Context {
        return sBaseContext ?: sAppContext
    }

    fun getResources() : Resources {
        return getContext().resources
    }

    fun getDisplayMetrics() : DisplayMetrics{
        return getResources().displayMetrics
    }

    fun dp2px(dpValue: Float): Int {
        val scale = getDisplayMetrics().density
        return (scale * dpValue + 0.5).toInt()
    }

    fun <T> getSystemService(service: String): T?{
        return getContext().getSystemService(service) as? T
    }

    val packageManager: PackageManager
        get() = getContext().packageManager

    val applicationInfo: ApplicationInfo
        get() = packageManager.getApplicationInfo(getContext().packageName, 0)
}

val Number.dp : Int inline get() = CommonUtils.dp2px(this.toFloat())