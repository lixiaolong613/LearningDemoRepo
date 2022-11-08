package com.example.madslearning.customview

import android.annotation.SuppressLint
import android.graphics.Matrix
import android.graphics.RectF
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.core.view.doOnAttach
import androidx.core.view.doOnDetach
import com.example.bitmapdemo.CommonUtils

/**
 * @Author lilong
 * @Date 2022/10/17
 * @Description
 */
@SuppressLint("LongLogTag")
class ImageMoveZoomFunc(val imageView: ImageView?): View.OnTouchListener, ViewTreeObserver.OnGlobalLayoutListener {
    companion object{
        const val TAG = "ImageMoveZoomEnableHelper"
    }
    private val customDetector = TouchDetector(CommonUtils.getContext())
    private val customDetectorListener = object : TouchDetector.DefaultDetectorListener() {
        override fun onTouchRelease() {
            adjustPositionWhenRelease()
            super.onTouchRelease()
        }

        override fun onMoveBegin(): Boolean {
            Log.i(TAG, "onMoveBegin")
            return true
        }

        override fun onMove(dx: Float, dy: Float) {
            Log.i(TAG, "onMove. dx = $dx, dy = $dy")
            imageView?.let { img ->
                img.drawable?.let {
                    scaleMatrix?.let {
                        it.postTranslate(dx, dy)
                        img.imageMatrix = it
                    }
                }
            }
        }

        override fun onMoveEnd() {
            Log.i(TAG, "onMoveEnd")
        }

        override fun onScaleBegin(): Boolean {
            Log.i(TAG,"onScaleBegin")
            return true
        }

        override fun onScale(scaleFactor: Float, focusX: Float, focusY: Float) {
            imageView?.let { img ->
                img.drawable?.let {
                    Log.i(
                        TAG,
                        "scaleFactor = $scaleFactor, width = ${img.width}, height = ${img.height} drawable.w = ${img.drawable?.intrinsicWidth}, drawable.h = ${img.drawable?.intrinsicHeight}"
                    )
                    var finaleFactor = scaleFactor
                    val minS = minScale?:(Int.MIN_VALUE + 1f)
                    val curScale = getCurScale()
                    if (curScale >= minS) {
                        if (scaleFactor <= 1 && curScale * scaleFactor < minS) {
                            finaleFactor = minS/curScale
                        }
                        scaleMatrix?.let {
                            it.postScale(finaleFactor, finaleFactor, focusX, focusY)
                            img.imageMatrix = it
                        }
                    }
                }
            }
        }

        override fun onScaleEnd() {
            Log.i(TAG,"onScaleEnd")
        }
    }

    private var scaleMatrix : Matrix? = null
    private val minRect: RectF by lazy { RectF(0f, 0f, imageView?.width?.toFloat()?:0f, imageView?.height?.toFloat()?:0f) }

    private var minScale: Float? = null


    init {
        imageView?.let { img ->
            img.scaleType = ImageView.ScaleType.MATRIX
            customDetector.scaleEnable = true
            customDetector.moveEnable = true
            customDetector.listener = customDetectorListener
        }
    }

    override fun onGlobalLayout() {
        imageView?.let { img ->
            img.drawable?.let {
                updateMinRect(minRect)
                resetImageInRect()
            }?: kotlin.run {
                Log.i(TAG, "drawable = null")
            }
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        event?.let {
            customDetector.onTouchEvent(it)
        }
        return true
    }

    /**
     * 处理矩阵的9个值
     */
    var martixValue = FloatArray(9)
    /**
     * 获取当前缩放比例
     */
    private fun getCurScale(): Float {
        scaleMatrix?.let {
            it.getValues(martixValue)
            return martixValue[Matrix.MSCALE_X]
        }
        return 1f
    }

    fun updateMinRect(rect: RectF) {
        minRect.set(rect)
        resetImageInRect()
    }

    private fun resetImageInRect() {
        imageView?.let { img ->
           img.drawable?.let { image ->
                //获取图片宽高
                val imgWidth: Int = image.intrinsicWidth
                val imgHeight: Int = image.intrinsicHeight
                //获取minRect宽高
                val width = minRect.width()
                val height = minRect.height()
                var scale = (width / imgWidth).coerceAtLeast(height / imgHeight)
                Log.i(TAG, "init scale = $scale, imgWidth = $imgWidth, width = $width")

                minScale = scale

                scaleMatrix = Matrix()
                scaleMatrix?.let {
                    it.postScale(scale, scale, 0f, 0f)//先对图片进行缩放，后面两个参数标识缩放中心点
                    //图片经过缩放后的中心点
                    val finalImgCenterX = scale * imgWidth / 2
                    val finalImgCenterY = scale * imgHeight / 2
                    //minRect的中心点
                    val rectCenterX = (minRect.left + minRect.right) / 2
                    val rectCenterY = (minRect.top + minRect.bottom) / 2
                    it.postTranslate(rectCenterX - finalImgCenterX, rectCenterY - finalImgCenterY)//再先把图片中心和minRect中心重合（图片移动到minRect中心）
                    //如果需要图片左上角与minRect重合则采用下面的方法
//                it.postTranslate(minRect.left, minRect.top)
                    img.imageMatrix = it
                }
            }
        }
    }

    fun adjustPositionWhenRelease() {
        //这里主要是通过mapRect计算出预期位置，然后在这里直接进行调整
        adjustScale()
        adjustPosition()
    }

    //如果图片任意一边的尺寸比minRect小，就以图片本身的中心点对图片进行放大
    private fun adjustScale() {
        imageView?.let{ img ->
            img.drawable?.let { d ->
                scaleMatrix?.let { mat ->
                    val rectF = RectF()
                    rectF.set(0f, 0f, d.intrinsicWidth.toFloat(), d.intrinsicHeight.toFloat())
                    mat.mapRect(rectF)//对rectF的矩形进行matrix变换，并将值写会rectF的四个顶点，用来模拟原始图片的缩放/平移/旋转操作
                    if (rectF.width() < minRect.width() || rectF.height() < minRect.height()) {
                        //计算放大倍数
                        var scale = (minRect.width() / rectF.width()).coerceAtLeast(minRect.height() / rectF.height())
                        mat.postScale(scale, scale, (rectF.left + rectF.right) / 2, (rectF.top + rectF.bottom) / 2)//以图片本身的中心点缩放
                        img.imageMatrix = mat
                    }
                }
            }
        }
    }

    //如果图片位置不能占满minRect，就把图片先移动到minRect左上角
    private fun adjustPosition() {
        imageView?.let { img ->
            img.drawable?.let { d ->
                scaleMatrix?.let { mat ->
                    val rectF = RectF()
                    rectF.set(0f, 0f, d.intrinsicWidth.toFloat(), d.intrinsicHeight.toFloat())
                    mat.mapRect(rectF)//对rectF的矩形进行matrix变换，并将值写会rectF的四个顶点，用来模拟原始图片的缩放/平移/旋转操作

                    var moveX = 0f
                    if (rectF.left > minRect.left) {
                        //需要向左移
                        moveX = minRect.left - rectF.left
                    } else if (rectF.right < minRect.right) {
                        //向右移
                        moveX = minRect.right - rectF.right
                    }

                    var moveY = 0f
                    if (rectF.top > minRect.top) {
                        //向上移
                        moveY = minRect.top - rectF.top
                    } else if (rectF.bottom < minRect.bottom) {
                        //向下移动
                        moveY = minRect.bottom - rectF.bottom
                    }

                    mat.postTranslate(moveX, moveY)
                    img.imageMatrix = mat
                }
            }
        }

    }

    init {

    }
}

@SuppressLint("LongLogTag")
open class Helper(private val imageView: ImageView?) {
    private var minRect: RectF? = null
//    by lazy {
//        RectF(0f, 0f, imageView?.width?.toFloat()?:0f, imageView?.height?.toFloat()?:0f)
//    }
    private var minScale: Float? = null

    fun getMinScale() = minScale


    fun updateMinRect(rect: RectF) {
        if (minRect == rect) {

        } else {
            if (minRect == null) {
                minRect = RectF()
            }
            minRect?.set(rect)
            imageView?.requestLayout()
            imageView?.invalidate()
        }
    }

    fun resetImageInRect(): Matrix? {
        var scaleMatrix: Matrix? = null
        imageView?.let { img ->
            img.drawable?.let { image ->
                minRect?.let { minR ->
                    //获取图片宽高
                    val imgWidth: Int = image.intrinsicWidth
                    val imgHeight: Int = image.intrinsicHeight
                    //获取minRect宽高
                    val width = minR.width()
                    val height = minR.height()
                    var scale = (width / imgWidth).coerceAtLeast(height / imgHeight)
                    Log.i(
                        ImageMoveZoomFunc.TAG,
                        "init scale = $scale, imgWidth = $imgWidth, width = $width"
                    )

                    minScale = scale

                    scaleMatrix = Matrix()
                    scaleMatrix?.let {
                        it.postScale(scale, scale, 0f, 0f)//先对图片进行缩放，后面两个参数标识缩放中心点
                        //图片经过缩放后的中心点
                        val finalImgCenterX = scale * imgWidth / 2
                        val finalImgCenterY = scale * imgHeight / 2
                        //minRect的中心点
                        val rectCenterX = (minR.left + minR.right) / 2
                        val rectCenterY = (minR.top + minR.bottom) / 2
                        it.postTranslate(
                            rectCenterX - finalImgCenterX,
                            rectCenterY - finalImgCenterY
                        )//再先把图片中心和minRect中心重合（图片移动到minRect中心）
                        //如果需要图片左上角与minRect重合则采用下面的方法
//                it.postTranslate(minRect.left, minRect.top)
                        img.imageMatrix = it
                    }
                }
            }
        }
        return scaleMatrix
    }

    fun adjustPositionWhenRelease(scaleMatrix: Matrix?) {
        //这里主要是通过mapRect计算出预期位置，然后在这里直接进行调整
        val mat = adjustScale(scaleMatrix)
        adjustPosition(mat)
    }

    //如果图片任意一边的尺寸比minRect小，就以图片本身的中心点对图片进行放大
    private fun adjustScale(scaleMatrix: Matrix?): Matrix? {
        imageView?.let{ img ->
            img.drawable?.let { d ->
                scaleMatrix?.let { mat ->
                    minRect?.let { minR ->
                        val rectF = RectF()
                        rectF.set(0f, 0f, d.intrinsicWidth.toFloat(), d.intrinsicHeight.toFloat())
                        mat.mapRect(rectF)//对rectF的矩形进行matrix变换，并将值写会rectF的四个顶点，用来模拟原始图片的缩放/平移/旋转操作
                        if (rectF.width() < minR.width() || rectF.height() < minR.height()) {
                            //计算放大倍数
                            var scale = (minR.width() / rectF.width()).coerceAtLeast(minR.height() / rectF.height())
                            mat.postScale(scale, scale, (rectF.left + rectF.right) / 2, (rectF.top + rectF.bottom) / 2)//以图片本身的中心点缩放
                            img.imageMatrix = mat
                        }
                    }
                }
            }
        }
        return scaleMatrix
    }

    //如果图片位置不能占满minRect，就把图片先移动到minRect左上角
    private fun adjustPosition(scaleMatrix: Matrix?): Matrix? {
        imageView?.let { img ->
            img.drawable?.let { d ->
                scaleMatrix?.let { mat ->
                    minRect?.let { minR ->
                        val rectF = RectF()
                        rectF.set(0f, 0f, d.intrinsicWidth.toFloat(), d.intrinsicHeight.toFloat())
                        mat.mapRect(rectF)//对rectF的矩形进行matrix变换，并将值写会rectF的四个顶点，用来模拟原始图片的缩放/平移/旋转操作

                        var moveX = 0f
                        if (rectF.left > minR.left) {
                            //需要向左移
                            moveX = minR.left - rectF.left
                        } else if (rectF.right < minR.right) {
                            //向右移
                            moveX = minR.right - rectF.right
                        }

                        var moveY = 0f
                        if (rectF.top > minR.top) {
                            //向上移
                            moveY = minR.top - rectF.top
                        } else if (rectF.bottom < minR.bottom) {
                            //向下移动
                            moveY = minR.bottom - rectF.bottom
                        }

                        mat.postTranslate(moveX, moveY)
                        img.imageMatrix = mat
                    }
                }
            }
        }
        return scaleMatrix
    }
}


//只保留基本的缩放/平移功能
@SuppressLint("LongLogTag")
fun ImageView.enableScaleAndMove(canMove: Boolean, canScale: Boolean, helper: Helper?) {
    var scaleMatrix = Matrix()
    //处理矩阵的9个值
    val matrixValue = FloatArray(9)
    //获取当前缩放比例
    fun getCurScale(): Float {
        scaleMatrix?.let {
            it.getValues(matrixValue)
            return matrixValue[Matrix.MSCALE_X]
        }
        return 1f
    }

    scaleType = ImageView.ScaleType.MATRIX

    val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        scaleMatrix = helper?.resetImageInRect() ?: scaleMatrix
    }

    this.doOnAttach {
        it.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
    }

    this.doOnDetach {
        it.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
    }

    fun adjustPositionWhenRelease() {
        helper?.adjustPositionWhenRelease(scaleMatrix)
    }

    val customDetector = TouchDetector(CommonUtils.getContext())

    val customDetectorListener = object : TouchDetector.DefaultDetectorListener() {
        override fun onTouchRelease() {
            adjustPositionWhenRelease()
        }

        override fun onMove(dx: Float, dy: Float) {
            Log.i(ImageMoveZoomFunc.TAG, "onMove. dx = $dx, dy = $dy")
            drawable?.let {
                scaleMatrix?.let {
                    it.postTranslate(dx, dy)
                    imageMatrix = it
                }
            }
        }


        override fun onScale(scaleFactor: Float, focusX: Float, focusY: Float) {
            drawable?.let {
                Log.i(
                    ImageMoveZoomFunc.TAG,
                    "scaleFactor = $scaleFactor, width = ${width}, height = ${height} drawable.w = ${drawable?.intrinsicWidth}, drawable.h = ${drawable?.intrinsicHeight}"
                )
                var finaleFactor = scaleFactor
                val minS = helper?.getMinScale()?:(Int.MIN_VALUE + 1f)
                val curScale = getCurScale()
                if (curScale >= minS) {
                    if (scaleFactor <= 1 && curScale * scaleFactor < minS) {
                        finaleFactor = minS/curScale
                    }
                    scaleMatrix?.let {
                        it.postScale(finaleFactor, finaleFactor, focusX, focusY)
                        imageMatrix = it
                    }
                }
            }
        }
    }

    customDetector.scaleEnable = canScale
    customDetector.moveEnable = canMove
    customDetector.listener = customDetectorListener

    this.setOnTouchListener { v, event ->
        event?.let {
            customDetector.onTouchEvent(it)
        }
        true
    }
}