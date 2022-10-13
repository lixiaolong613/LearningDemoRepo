package com.example.bitmapdemo

import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat


object BitmapUtils {
    fun mergeBitmap(src: Bitmap, mask: Bitmap): Bitmap {
        val result = Bitmap.createBitmap(
            src.width,
            src.height,
            Bitmap.Config.ARGB_8888
        )
        val mCanvas = Canvas(result)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.ADD)
        mCanvas.drawBitmap(src,0f,0f,null)
        mCanvas.drawBitmap(mask, 0f, 0f, paint)
        paint.xfermode = null
        return result
    }

    fun mergeBitmapWithMaskColor(src: Bitmap?, @ColorInt color: Int): Bitmap? {
        src?.let {
            val result = Bitmap.createBitmap(
                src.width,
                src.height,
                Bitmap.Config.ARGB_8888
            )
            val mCanvas = Canvas(result)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.ADD)
            mCanvas.drawBitmap(src,0f,0f,null)
            mCanvas.drawColor(color, PorterDuff.Mode.SRC_OVER)
            paint.xfermode = null
            return result
        }
        return null
    }

    fun drawableToBitmap(id: Int): Bitmap? {
        // 取 drawable 的长宽
        val drawable: Drawable? = ResourcesCompat.getDrawable(CommonUtils.getResources(),id, null)
        drawable?.let {
            val w = drawable.intrinsicWidth
            val h = drawable.intrinsicHeight
            return drawableToBitmap(drawable, w, h)
        }
        return null
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap? {
        // 取 drawable 的长宽
        val w = drawable.intrinsicWidth
        val h = drawable.intrinsicHeight
        return drawableToBitmap(drawable, w, h)
    }

    fun drawableToBitmap(drawable: Drawable, width: Int, height: Int): Bitmap? {
        // 取 drawable 的颜色格式
        val config =
            if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
        return drawableToBitmap(drawable, width, height, config)
    }

    fun drawableToBitmap(
        drawable: Drawable,
        width: Int,
        height: Int,
        config: Bitmap.Config?
    ): Bitmap? {
        // 建立对应 bitmap
        val bitmap = Bitmap.createBitmap(width, height, config!!)
        // 建立对应 bitmap 的画布
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, width, height)
        // 把 drawable 内容画到画布中
        drawable.draw(canvas)
        return bitmap
    }
}