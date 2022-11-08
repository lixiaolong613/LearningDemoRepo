package com.example.bitmapdemo.customview

import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.NinePatchDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.*
import androidx.core.graphics.createBitmap
import androidx.core.view.drawToBitmap
import com.example.madslearning.customview.TouchDetector
import java.io.File
import kotlin.io.path.toPath


/**
 * @Author lilong
 * @Date 2022/10/13
 * @Description
 */
class ZoomImageView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyleAttr), View.OnTouchListener, ViewTreeObserver.OnGlobalLayoutListener{

    companion object{
        private const val TAG = "ZoomImageView"
        const val ACTION_NONE = 0
        const val ACTION_MOVE = 1
        const val ACTION_SCALE = 2
    }

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)

    private val customDetector = TouchDetector(context)
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
            drawable?.let {
                scaleMatrix?.let {
                    it.postTranslate(dx, dy)
                    imageMatrix = it
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
            drawable?.let {
                Log.i(
                    TAG,
                    "scaleFactor = $scaleFactor, width = ${width}, height = $height drawable.w = ${drawable?.intrinsicWidth}, drawable.h = ${drawable?.intrinsicHeight}"
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
                        imageMatrix = it
                    }
                }
            }
        }

        override fun onScaleEnd() {
            Log.i(TAG,"onScaleEnd")
        }
    }

    init {
        scaleType = ScaleType.MATRIX
        setOnTouchListener(this)
        customDetector.scaleEnable = true
        customDetector.moveEnable = true
        customDetector.listener = customDetectorListener
        var file: File? = null
        file?.let {
            it.toURI().toString()
            it.path
        }
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
            updateMinRect(minRect)
            resetImageInRect()
        }?: kotlin.run {
            Log.i(TAG, "drawable = null")
        }
    }

    private val minRect: RectF by lazy { RectF(0f, 0f, width.toFloat(), height.toFloat()) }

    fun updateMinRect(rect: RectF) {
        minRect.set(rect)
        resetImageInRect()
    }

    private fun resetImageInRect() {
        drawable?.let { image ->
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
                imageMatrix = it
            }
        }
    }

    private var scaleMatrix : Matrix? = null

    private var minScale: Float? = null

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

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        event?.let {
            customDetector.onTouchEvent(it)
        }
        return true
    }

    fun adjustPositionWhenRelease() {
        //这里主要是通过mapRect计算出预期位置，然后在这里直接进行调整
        adjustScale()
        adjustPosition()
    }

    //如果图片任意一边的尺寸比minRect小，就以图片本身的中心点对图片进行放大
    private fun adjustScale() {
        drawable?.let { d ->
            scaleMatrix?.let { mat ->
                val rectF = RectF()
                rectF.set(0f, 0f, d.intrinsicWidth.toFloat(), d.intrinsicHeight.toFloat())
                mat.mapRect(rectF)//对rectF的矩形进行matrix变换，并将值写会rectF的四个顶点，用来模拟原始图片的缩放/平移/旋转操作
                if (rectF.width() < minRect.width() || rectF.height() < minRect.height()) {
                    //计算放大倍数
                    var scale = (minRect.width() / rectF.width()).coerceAtLeast(minRect.height() / rectF.height())
                    mat.postScale(scale, scale, (rectF.left + rectF.right) / 2, (rectF.top + rectF.bottom) / 2)//以图片本身的中心点缩放
                    imageMatrix = mat
                }
            }
        }

    }

    //如果图片位置不能占满minRect，就把图片先移动到minRect左上角
    private fun adjustPosition() {
        drawable?.let { d ->
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
                imageMatrix = mat
            }

        }
    }
}

