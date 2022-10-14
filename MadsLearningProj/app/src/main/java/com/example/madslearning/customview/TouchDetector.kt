package com.example.madslearning.customview

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.ViewConfiguration
import kotlin.math.abs
import kotlin.math.hypot

/**
 * @Author lilong
 * @Date 2022/10/14
 * @Description
 */
class TouchDetector(context: Context) {
    companion object{
        const val TAG = "TouchDetector"
    }

    abstract class DefaultDetectorListener: DetectorListener {
        override fun onMoveBegin() = true

        override fun onMove(dx: Float, dy: Float){}

        override fun onMoveEnd(){}

        override fun onScaleBegin(): Boolean = true

        override fun onScale(scaleFactor: Float, focusX: Float, focusY: Float){}

        override fun onScaleEnd(){}
    }

    interface DetectorListener {

        fun onMoveBegin(): Boolean

        fun onMove(dx: Float, dy: Float)

        fun onMoveEnd()

        fun onScaleBegin(): Boolean

        fun onScale(scaleFactor: Float, focusX: Float, focusY: Float)

        fun onScaleEnd()
    }

    var listener: DetectorListener? = null
    var moveEnable = false
    var scaleEnable = false


    //是否正在拖动(多指情况下，只接受primary pointer. 且enableScale=true的情况下，多指操作会暂停move)
    private var inMoveProcess = false
    private var movePointerId = 0
    private var moveLastX = 0f
    private var moveLastY = 0f
    private var moveCurX = 0f
    private var moveCurY = 0f
    private val moveProcessValid
        get() = moveEnable && inMoveProcess
    private fun checkMovePointerId(event: MotionEvent): Boolean {
        return event.findPointerIndex(movePointerId) >= 0
    }
    private fun reCalculateMoveCurPoint(event: MotionEvent) {
        val index = event.findPointerIndex(movePointerId)
        if (index >= 0) {
            moveCurX = event.getX(index)
            moveCurY = event.getY(index)
        } else {
            Log.e(TAG, "reCalculateMovePoint error! can not find pointerId = $movePointerId in event = $event")
        }
    }
    private fun initMovePointerId(event: MotionEvent, pointerIndex: Int = 0) {
        if (pointerIndex <= event.pointerCount) {
            movePointerId = event.getPointerId(pointerIndex)
            moveLastX = event.getX(pointerIndex)
            moveLastY = event.getY(pointerIndex)
        } else {
            showErrorLog("resetMoveState error. pointerIndex > event.pointerCount")
        }
    }
    private fun resetMoveState() {
        inMoveProcess = false
        movePointerId = 0
        moveLastX = 0f
        moveLastY = 0f
        moveCurX = 0f
        moveCurY = 0f
    }



    private var scaleSlop = 0
    private var moveSlop = 0
    private var minSpan = 0
    //是否正在缩放
    private var inScaleProcess = false
    private var scaleFocusX = 0f
    private var scaleFocusY = 0f
    private var scalePreSpan = 0f
    private var scaleCurSpan = 0f
    private val scaleProcessValid
        get() = scaleEnable && inScaleProcess
    private val scaleFactor
        get() = if (scalePreSpan > 0) scaleCurSpan / scalePreSpan else 1f
    private fun reCalculateScaleFocus(event: MotionEvent, skipIndex: Int? = null) {
        var sumX = 0f
        var sumY = 0f
        var count = event.pointerCount
        for (index in 0 until event.pointerCount) {
            if (index != skipIndex) {
                sumX += event.getX(index)
                sumY += event.getY(index)
            } else {
                count -= 1
            }
        }
        if (count > 0) {
            scaleFocusX = sumX / count
            scaleFocusY = sumY / count
        } else {
            showErrorLog("reCalculateScaleFocus when count = 0")
        }
    }
    private fun reCalculateScaleCurSpan(event: MotionEvent, skipIndex: Int? = null) {
        var devX = 0f
        var devY = 0f
        var count = event.pointerCount
        for (index in 0 until event.pointerCount) {
            if (index != skipIndex) {
                devX += abs(event.getX(index) - scaleFocusX)
                devY += abs(event.getY(index) - scaleFocusY)
            } else {
                count -= 1
            }
        }
        if (count > 0) {
            devX /= count
            devY /= count
            scaleCurSpan = hypot(devX.toDouble(), devY.toDouble()).toFloat()
        } else {
            showErrorLog("reCalculateScaleCurSpan when count = 0")
        }
    }
    private fun resetScaleState() {
        inScaleProcess = false
        scaleFocusX = 0f
        scaleFocusY = 0f
        scalePreSpan = 0f
        scaleCurSpan = 0f
    }

    init {
        val viewConfiguration = ViewConfiguration.get(context)
        moveSlop = viewConfiguration.scaledTouchSlop
        scaleSlop = moveSlop / 2
//        val targetSdkVersion = context.applicationInfo.targetSdkVersion
//        if (targetSdkVersion > Build.VERSION_CODES.Q) {
//            minSpan = viewConfiguration.scaledMinimumScalingSpan
//        }
    }

    fun onTouchEvent(event: MotionEvent) {
        if (!moveEnable && !scaleEnable) {
            return
        }

        val action = event.actionMasked
        val pointerCount = event.pointerCount

        val streamComplete = action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP
        if (streamComplete) {
            // If it's streamComplete, we need to reset the state.
            if (moveProcessValid) {
                listener?.onMoveEnd()
            }
            //init moveProcess state
            resetMoveState()

            if (scaleProcessValid) {
                listener?.onScaleEnd()
            }
            //init scaleProcess state
            resetScaleState()

            return
        }

        when(action) {
            MotionEvent.ACTION_DOWN -> {
                Log.i(TAG, "ACTION_DOWN, count = $pointerCount")
                // If it's a ACTION_DOWN, we are beginning a new event stream.
                // If we are in MoveProcess or inScaleProcess when beginning a new event stream,
                // it means the app probably didn't give us all the motion events. Shame on it.
                //just in case
                if (moveProcessValid) {
                    listener?.onMoveEnd()
                }
                //init moveProcess state
                initMovePointerId(event, 0)
                inMoveProcess = false


                if (scaleProcessValid) {
                    listener?.onScaleEnd()
                }
                //init scaleProcess state
                resetScaleState()
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                Log.i(TAG, "ACTION_POINTER_DOWN, count = $pointerCount")
                if (moveEnable && scaleEnable) {
                    //when scaleEnable is true, stop the move stream when multi pointers down
                    if (moveProcessValid) {
                        listener?.onMoveEnd()
                    }
                    //no need reset
//                    resetMoveState(event, 0)
                }

                if (scaleEnable) {
                    //recalculate scaleFocus and preSpan
                    reCalculateScaleFocus(event)
                    reCalculateScaleCurSpan(event)
                    scalePreSpan = scaleCurSpan
                    if (inScaleProcess) {
                        if (scaleCurSpan < minSpan || (pointerCount - 1) <= 1)  {
                            listener?.onScaleEnd()
                            inScaleProcess = false
                        }
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                Log.i(TAG, "ACTION_MOVE, count = $pointerCount")
                if (scaleEnable) {
                    reCalculateScaleFocus(event)
                    reCalculateScaleCurSpan(event)
                    if (!inScaleProcess && checkScaleSlop(scaleCurSpan, scalePreSpan) && scaleCurSpan >= minSpan) {
                        //start scale stream
                        inScaleProcess = listener?.onScaleBegin() ?: false
                    }
                    if (inScaleProcess) {
                        if (scaleCurSpan < minSpan) {
                            listener?.onScaleEnd()
                            inScaleProcess = false
                        } else {
                            listener?.onScale(scaleFactor, scaleFocusX, scaleFocusY)
                        }
                        scalePreSpan = scaleCurSpan
                    }
                }

                if (moveEnable && (!scaleEnable || pointerCount == 1)) {
                    //deal with the move stream here
                    if (checkMovePointerId(event)) {
                        Log.i(TAG, "checkMovePointerId = true, index = ${event.findPointerIndex(movePointerId)}")
                        reCalculateMoveCurPoint(event)
                        if (!inMoveProcess && checkMoveSlop(moveCurX, moveCurY, moveLastX, moveLastY)) {
                            Log.i(TAG, "slop = $moveSlop, delX = ${abs(moveCurX - moveLastX)}, delY = ${abs(moveCurY - moveLastY)}")
                            inMoveProcess = listener?.onMoveBegin()?:false
                        }
                        if (inMoveProcess) {
                            listener?.onMove(moveCurX - moveLastX, moveCurY - moveLastY)
                            moveLastX = moveCurX
                            moveLastY = moveCurY
                        }
                    } else {
                        Log.i(TAG, "checkMovePointerId = false")
                        initMovePointerId(event)
                    }
                }
            }
            MotionEvent.ACTION_POINTER_UP -> {
                Log.i(TAG, "ACTION_POINTER_UP, count = $pointerCount")
                if (moveEnable) {
                    //find whether last movePointerId is still valid
                    val lastMoveIndex = event.findPointerIndex(movePointerId)
                    if (lastMoveIndex >= 0 && lastMoveIndex != event.actionIndex) {
                        //here mean to update moveLastX and moveLastY
                        initMovePointerId(event, lastMoveIndex)
                        Log.i(TAG, "ACTION_POINTER_UP. movePointerId valid")
                    } else {
                        for (index in 0 until pointerCount) {
                            if (index != event.actionIndex) {
                                //find the first valid index
                                initMovePointerId(event, index)
                                Log.i(TAG, "ACTION_POINTER_UP. movePointerId invalid, newIndex = $index")
                                break
                            }
                        }
                    }
                }

                if (scaleEnable) {
                    //recalculate scaleFocus and preSpan
                    reCalculateScaleFocus(event, event.actionIndex)
                    reCalculateScaleCurSpan(event, event.actionIndex)
                    scalePreSpan = scaleCurSpan
                    if (inScaleProcess) {
                        if (scaleCurSpan < minSpan || (pointerCount - 1) <= 1) {
                            listener?.onScaleEnd()
                            inScaleProcess = false
                        }
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                Log.i(TAG, "ACTION_UP, count = $pointerCount")
            }
            MotionEvent.ACTION_CANCEL -> {
                Log.i(TAG, "ACTION_CANCEL, count = $pointerCount")
            }
        }
    }

    private fun checkScaleSlop(cur: Float, pre: Float): Boolean {
        return abs(cur - pre) >= scaleSlop
    }

    private fun checkMoveSlop(curX: Float, curY: Float, lastX: Float, lastY: Float): Boolean {
        val deltaX = abs(curX - lastX)
        val deltaY = abs(curY - lastY)
        return (hypot(deltaX, deltaY) >= moveSlop)
    }

    private fun showErrorLog(content: String) {
        Log.e(TAG, content)
        throw Throwable(content)
    }

}