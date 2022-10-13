package com.example.bitmapdemo

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.FileDescriptor

class ImageResizer {
    companion object{
        fun decodeBitmapFromRes(res: Resources, id: Int, reqWidth: Int, reqHeight: Int) : Bitmap? {
            val op = BitmapFactory.Options().apply{
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeResource(res, id, op)
            op.inSampleSize = getScale(reqWidth, reqHeight, op.outWidth, op.outHeight)
            op.inJustDecodeBounds = false
            return BitmapFactory.decodeResource(res, id, op)
        }

        fun decodeBitmapFromFileDescriptor(fd: FileDescriptor, reqWidth: Int, reqHeight: Int) : Bitmap? {
            val op = BitmapFactory.Options().apply{
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFileDescriptor(fd, null, op)
            op.inSampleSize = getScale(reqWidth, reqHeight, op.outWidth, op.outHeight)
            op.inJustDecodeBounds = false
            return BitmapFactory.decodeFileDescriptor(fd, null, op)
        }

        /**
         * 根据图片的原始宽高和需要显示的宽高计算缩小比例。
         * @return 2的整数幂
         */
        private fun getScale(reqWidth: Int, reqHeight: Int, width: Int, height: Int): Int {
            var scale = 1
            if (reqWidth < width || reqHeight < height) {
                val halfW = width / 2
                val halfH = height / 2
                if (halfW / scale > reqWidth && halfH / scale > reqHeight) {
                    scale *= 2
                }
            }
            return scale
        }
    }
}