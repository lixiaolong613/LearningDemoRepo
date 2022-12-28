package com.example.madslearning.utils

import android.graphics.Matrix
import android.graphics.Point
import android.graphics.PointF
import android.util.Log
import android.view.View
import com.example.bitmapdemo.CommonUtils
import kotlin.math.abs
import kotlin.math.atan2

object MathematicsUtils {
    const val TAG = "MathematicsUtils"
    private val oneCircleInRadian = 2 * Math.PI
    fun getRotateDegFromView(startView: View, endView: View): Double {
//        if (startView.measuredWidth == 0) {
//            startView.measure(
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
//            )
//            Log.i(TAG, "startView measure. id = ${CommonUtils.getContext().resources.getResourceName(startView.id)}")
//        }
//        if (endView.measuredWidth == 0) {
//            endView.measure(
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
//            )
//            Log.i(TAG, "endView measure. id = ${CommonUtils.getContext().resources.getResourceName(endView.id)}")
//        }

        val startPosition = IntArray(2)
        startView.getLocationOnScreen(startPosition)
        val startPoint = PointF(startPosition[0] + startView.measuredWidth/2f, startPosition[1] + startView.measuredHeight/2f)

        val endPosition = IntArray(2)
        endView.getLocationOnScreen(endPosition)
        val endPoint = PointF(endPosition[0] + endView.measuredWidth/2f, endPosition[1] + endView.measuredHeight/2f)

        Log.i(TAG, "startView w = ${startView.measuredWidth}, h = ${startView.measuredHeight}, startCenterP = $startPoint\n" +
                "endView w = ${endView.measuredWidth}, h = ${endView.measuredHeight}, encCenterP = $endPoint")
        return getRotateDegree(startPoint, endPoint)
    }

    /**
     * 以正上（北）方向为基准，旋转的角度
     * 顺时针旋转 0~360
     * startPoint,endPoint分别是canvas坐标系下的点
     */
    private fun getRotateDegree(startPoint: PointF, endPoint: PointF): Double {
//        //转换为右手坐标系下的delta
//        val xDelta = endPoint.x - startPoint.x
//        val yDelta = -(endPoint.y - startPoint.y)
//        //-pi ~ pi
//        val tanRadian = -atan2(yDelta.toDouble(), xDelta.toDouble()).also {
//            Log.i(TAG, "xD = $xDelta, yD = $yDelta, deg = ${Math.toDegrees(it)}")
//        }
//        //从-pi~pi转换为0~2*pi
//        var tmpRadian = tanRadian + if (tanRadian < 0) (oneCircleInRadian) else 0.0
//        //从以x轴为基准，转换为以y为基准
//        tmpRadian += Math.PI/2
//        while (tmpRadian >= oneCircleInRadian) {
//            tmpRadian -= (oneCircleInRadian)
//        }
//        return Math.toDegrees(tmpRadian)


        //转换为右手坐标系下的delta
        val xDelta = endPoint.x - startPoint.x
        val yDelta = -(endPoint.y - startPoint.y)
        //坐标变换，逆时针旋转90度
        val xChange = yDelta
        val yChange = -xDelta
        //-pi~pi，逆时针为正，顺时针为负
        val radian = atan2(yChange.toDouble(), xChange.toDouble())
        //转换方向，顺时针为正，逆时针为负
        var tmpRadian = -radian
        //转换为0~2*pi
        while (tmpRadian < 0) {
            tmpRadian += oneCircleInRadian
        }

        return Math.toDegrees(tmpRadian)
    }
}