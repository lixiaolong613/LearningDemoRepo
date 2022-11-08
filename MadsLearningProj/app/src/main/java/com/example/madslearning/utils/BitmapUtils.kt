package com.example.bitmapdemo

import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat


object BitmapUtils {
//    fun mergeBitmap(src: Bitmap, mask: Bitmap): Bitmap {
//        val result = Bitmap.createBitmap(
//            src.width,
//            src.height,
//            Bitmap.Config.ARGB_8888
//        )
//        val mCanvas = Canvas(result)
//        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
//        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.ADD)
//        mCanvas.drawBitmap(src,0f,0f,null)
//        mCanvas.drawBitmap(mask, 0f, 0f, paint)
//        paint.xfermode = null
//        return result
//    }

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

    fun addMaskToBitmap(bitmap: Bitmap, @ColorInt maskColor: Int): Bitmap? {
        val res = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(res)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        canvas.drawColor(maskColor, PorterDuff.Mode.SRC_OVER)
        return res
    }

    fun waterMarkBitmap(
        text: String,
        textSize: Int,
        textColor: Int,
        width: Int,
        height: Int
    ): Bitmap {
        //把width和height组成的矩形放大为以长边为变的正方形
        val factor = Math.sqrt(2.0).toFloat()
        val sideLength = Math.hypot(width.toDouble(), height.toDouble()).toInt() //图片的外接正方形边长
        val sideWidth = (width * 1.5).toInt()
        val sideHeight = (height * 1.5).toInt()
        val rotate = 0
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val rect = Rect()
        paint.textSize = textSize.toFloat()
        //获取文字长度和宽度
        paint.getTextBounds(text, 0, text.length, rect)
        val strHeight = rect.height()
        val strWidth = rect.width()
        val strHorMargin = 30
        val strVerMargin = 30
        var markBitmap = Bitmap.createBitmap(sideWidth, sideHeight, Bitmap.Config.ARGB_8888)
        try {
            val canvas = Canvas(markBitmap)
            canvas.drawColor(Color.TRANSPARENT) //添加透明画布
            paint.color = textColor
            // 获取跟清晰的图像采样
            paint.isDither = true
            paint.isFilterBitmap = true
            canvas.save()
            val moveX = width / 2f - sideWidth / 2f
            val moveY = height / 2f - sideHeight / 2f
            canvas.translate(moveX, moveY)
            val centerX = width / 2
            val centerY = height / 2
            canvas.rotate(rotate.toFloat(), centerX.toFloat(), centerY.toFloat())
            var i = 0
            while (i < sideWidth) {
                var count = 0
                var j = 0
                while (j < sideHeight) {
                    if (count % 2 == 0) {
                        canvas.drawText(text, i.toFloat(), j.toFloat(), paint)
                    } else {
                        canvas.drawText(text, i + strWidth / 2f, j.toFloat(), paint)
                    }
                    j = (j + strHeight + strVerMargin)
                    count++
                }
                i = (i + strWidth + strHorMargin)
            }
            canvas.restore()
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
        }
        return markBitmap
    }

    fun mergeBitmap(src: Bitmap, merged: Bitmap): Bitmap {
        val res = Bitmap.createBitmap(
            src.width,
            src.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(res)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
        canvas.drawBitmap(src, 0f, 0f, null)
        canvas.drawBitmap(merged!!, 0f, 0f, paint)
        return res
    }
}