package com.example.madslearning.utils

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.Nullable


/**
 * @Author lilong
 * @Date 2023/1/3
 * @Description
 *
 * View.SYSTEM_UI_FLAG_LAYOUT_STABLE
 * 作用: 稳定布局。当StatusBar和NavigationBar动态显示和隐藏时
 * 系统为fitSystemWindow=true的view设置的padding大小都不会变化，所以view的内容的位置也不会发生移动。

 * View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
 * 作用：在不隐藏StatusBar的情况下，将view所在window的显示范围扩展到StatusBar下面。同时Activity的部分内容也因此被StatusBar覆盖遮挡。
 * 当使用此Flag时，设置fitSystemWindow=true的view，会被系统自动添加大小为statusBar和ActionBar高度之和相同的paddingTop。
 * 当window设置WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS时，此Flag会被系统会自动添加。
 *
 * WindowMananger.FLAG_TRANSLUCENT_STATUS
 * 半透明StatusBar,并且不会因用户交互而被清除。
 * 设置了此flag,系统会自动设置View.SYSTEM_UI_FLAG_LAYOUT_STABLE和View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

 * View.SYSTEM_UI_FLAG_HIDE_NAVIGATION //隐藏导航栏
 View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION//不隐藏导航的情况下,将Activity的显示范围扩展到导航栏底部,但是Activity的底部内容会被遮挡.
 View.SYSTEM_UI_FLAG_FULLSCREEN//隐藏状态栏 如果需要一直隐藏  可以使用WindowManager.LayoutParams.FLAG_FULLSCREEN
 View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN//不隐藏状态栏的情况下 将Activity内容显示在状态栏下 ,但是内容会被遮挡
 View.SYSTEM_UI_FLAG_IMMERSIVE//避免用户交互导致清楚全屏状态,配合全屏时使用
 View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY//避免某些⽤户交互造成系统⾃动清除全屏状态。同时Activity的部分内容也因此被StatusBar覆盖遮挡。
 View.SYSTEM_UI_FLAG_LAYOUT_STABLE//稳定布局,在动态显示隐藏 状态栏导航栏的情况下,系统为设置了fitSysetmWindow=true的View固定Padding值.使内容不上下移动

 */
object FullScreenUtils {
    /**
     * 设置让应用内容可以绘制到系统UI区域，确保状态栏/导航栏显隐的时候，界面不跳变，一般与<br/>
     * {@link #enableTransparentStatusBar(Activity, boolean)}、<br/>
     * {@link #enableTransparentNaviBar(Activity, boolean)} <br/>
     * 配合使用
     * @param activity 当前界面
     * @param drawToStatus 状态栏区域不跳变
     * @param drawToNavi 导航栏区域不跳变
     */
    fun enableDrawToSysUi(@Nullable window: Window?, drawToStatus: Boolean, drawToNavi: Boolean) {
        if (window == null) {
            return
        }
        val decorView: View = window.getDecorView()
        var newSysUi: Int = decorView.getSystemUiVisibility()
        newSysUi = newSysUi or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        if (drawToStatus) {
            newSysUi = newSysUi or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (drawToNavi) {
            newSysUi = newSysUi or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }
        setDecorSysUiVisibility(decorView, newSysUi)
    }

    /**
     * 使状态栏背景透明
     * @param activity 当前界面
     * @param lightContent 即将显示在状态栏区域的内容是否为浅色的，如果不确定，则传`false`即可。
     */
    fun enableTransparentStatusBar(activity: Activity, lightContent: Boolean) {
        val window = activity.window
        enableTransparentStatusBar(window, lightContent)
    }

    /**
     * 使状态栏背景透明
     * @param window 当前Window
     * @param lightContent 即将显示在状态栏区域的内容是否为浅色的，如果不确定，则传`false`即可。
     */
    fun enableTransparentStatusBar(window: Window?, lightContent: Boolean) {
        if (window == null) {
            return
        }
        val decorView = window.decorView
        var newSysUi = decorView.systemUiVisibility
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            newSysUi = if (lightContent) {
                newSysUi or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                newSysUi and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }
        setDecorSysUiVisibility(decorView, newSysUi)
    }

    /**
     * 使导航栏背景透明
     * @param window 当前Window
     * @param lightContent 即将显示在导航栏区域的内容是否为浅色的，如果不确定，则传`false`即可。
     */
    fun enableTransparentNaviBar(window: Window?, lightContent: Boolean) {
        if (window == null) {
            return
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                val decorView = window.decorView
                var newSysUi = decorView.systemUiVisibility
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    window.navigationBarColor = Color.TRANSPARENT
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    newSysUi = if (lightContent) {
                        newSysUi or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                    } else {
                        newSysUi and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
                    }
                }
                setDecorSysUiVisibility(decorView, newSysUi)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setDecorSysUiVisibility(decorView: View, sysUiVisibility: Int) {
        decorView.systemUiVisibility = sysUiVisibility
        decorView.requestApplyInsets()
    }
}