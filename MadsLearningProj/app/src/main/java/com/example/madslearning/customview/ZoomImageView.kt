package com.example.bitmapdemo.customview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.ImageView

/**
 * @Author lilong
 * @Date 2022/10/13
 * @Description
 */
class ZoomImageView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyleAttr){

    companion object{
        const val ACTION_NONE = 0
        const val ACTION_MOVE = 1
        const val ACTION_SCALE = 2
    }



}

