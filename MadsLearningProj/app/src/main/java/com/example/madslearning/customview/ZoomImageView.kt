package com.example.bitmapdemo.customview

import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewTreeObserver


/**
 * @Author lilong
 * @Date 2022/10/13
 * @Description
 */
class ZoomImageView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyleAttr), View.OnTouchListener, ScaleGestureDetector.OnScaleGestureListener, ViewTreeObserver.OnGlobalLayoutListener{

    companion object{
        const val TAG = "ZoomImageView"
        const val ACTION_NONE = 0
        const val ACTION_MOVE = 1
        const val ACTION_SCALE = 2
    }

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)

    init {
        scaleType = ScaleType.MATRIX
        setOnTouchListener(this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewTreeObserver.removeOnGlobalLayoutListener(this);
    }

    override fun onGlobalLayout() {
        drawable?.let {
            //获取图片宽高
            val imgWidth: Int = it.intrinsicWidth
            val imgHeight: Int = it.intrinsicHeight
            //获取imageview宽高
            val width = width
            val height = height

            var scale = 1f
            scale = (width.toFloat() / imgWidth).coerceAtLeast(height.toFloat() / imgHeight)
            Log.i(TAG, "init scale = $scale")
            if (scaleMatrix == null) {
                scaleMatrix = Matrix()
                minScale = scale
            }
            scaleMatrix?.let {
                it.postTranslate((width - imgWidth) / 2f, (height - imgHeight) / 2f)//先把图片移动到控件中心
                it.postScale(scale, scale, width/2f, height/2f)//后面两个参数标识缩放中心点
                imageMatrix = it
            }
        }?: kotlin.run {
            Log.i(TAG, "drawable = null")
        }
    }

    /**
     * 手势检测
     */
    var scaleGestureDetector = ScaleGestureDetector(context, this)

    private var scaleMatrix : Matrix? = null

    private var minScale: Float? = null

    /**
     * 处理矩阵的9个值
     */
    var martixValue = FloatArray(9)
    /**
     * 获取当前缩放比例
     */
    fun getCurScale(): Float {
        scaleMatrix?.let {
            it.getValues(martixValue)
            return martixValue[Matrix.MSCALE_X]
        }
        return 1f
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return scaleGestureDetector.onTouchEvent(event)
    }

    override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
        val scaleFactor = detector?.scaleFactor?:1f
        Log.i(TAG, "onScaleBegin. scaleFactor = $scaleFactor")
        return true
    }



    override fun onScale(detector: ScaleGestureDetector?): Boolean {
        drawable?.let {
            detector?.let { det ->
                var scaleFactor = detector.scaleFactor
                Log.i(
                    TAG,
                    "scaleFactor = $scaleFactor, width = ${width}, height = $height drawable.w = ${drawable?.intrinsicWidth}, drawable.h = ${drawable?.intrinsicHeight}"
                )
                val minS = minScale?:(Int.MIN_VALUE + 1f)
                val curScale = getCurScale()
                if (curScale >= minS) {
                    if (scaleFactor <= 1 && curScale * scaleFactor < minS) {
                        scaleFactor = minS/curScale
                    }
                    scaleMatrix?.let {
                        it.postScale(scaleFactor, scaleFactor, det.focusX, det.focusY)
                        adjustPosition()
                        imageMatrix = it
                    }
                }
            }
        }
        return true
    }

    fun adjustPosition() {
        //这里主要是通过mapRect计算出预期位置，然后在这里直接进行调整
        val rectF = RectF()
        val d = drawable
        if (d != null) {
            rectF[0f, 0f, d.intrinsicWidth.toFloat()] = d.intrinsicHeight.toFloat()
            matrix.mapRect(rectF)//对rectF的矩形进行matrix变换，并将值写会rectF的四个顶点，用来模拟原始图片的缩放/平移/旋转操作
        }
    }

    override fun onScaleEnd(detector: ScaleGestureDetector?) {

    }
}

