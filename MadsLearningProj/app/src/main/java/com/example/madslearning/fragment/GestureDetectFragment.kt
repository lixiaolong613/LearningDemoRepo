package com.example.madslearning.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.example.madslearning.databinding.FragmentGestureDetectBinding

@SuppressLint("ClickableViewAccessibility")
class GestureDetectFragment: Fragment(), GestureDetector.OnGestureListener, ScaleGestureDetector.OnScaleGestureListener {
    companion object{
        const val TAG = "GestureDetectFragment"
    }

    private var isScale = false
    val detector by lazy {
        GestureDetector(context, this)
    }
    val scaleDetector by lazy {
        ScaleGestureDetector(context, this)
    }

    private lateinit var binding: FragmentGestureDetectBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGestureDetectBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvGesture.setOnClickListener {
            isScale = false
        }
        binding.tvScale.setOnClickListener {
            isScale = true
        }
        binding.ivImage.setOnTouchListener { v, event ->
            if (isScale) scaleDetector.onTouchEvent(event) else detector.onTouchEvent(event)
        }
    }


    /****************GestureDetector.OnGestureListener*******************/
    //用户按下屏幕就会触发
    override fun onDown(e: MotionEvent?): Boolean {
        Log.i(TAG, "onDown")
        return false;
    }
    //用户按下触摸屏、快速移动后松开,由1个MotionEvent ACTION_DOWN,
    //多个ACTION_MOVE, 1个ACTION_UP触发
    //e1：第1个ACTION_DOWN MotionEvent
    //e2：最后一个ACTION_MOVE MotionEvent
    //velocityX：X轴上的移动速度，像素/秒
    //velocityY：Y轴上的移动速度，像素/秒
    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        Log.i(TAG, "onFling:\n e1.x = ${e1?.x}, e1.y = ${e1?.y}, e2.x = ${e2?.x}, e2.y = ${e2?.y}")
        return false
    }
    //用户长按触摸屏，由多个MotionEvent ACTION_DOWN触发
    override fun onLongPress(e: MotionEvent?) {
        Log.i(TAG, "onLongPress")
    }
    //用户按下触摸屏，并拖动，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE触发
    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        Log.i(TAG, "onScroll:\n e1.x = ${e1?.x}, e1.y = ${e1?.y}, e2.x = ${e2?.x}, e2.y = ${e2?.y}")
        return false
    }
    //如果是按下的时间超过瞬间，而且在按下的时候没有松开或者是拖动的，
    // 那么onShowPress就会执行
    override fun onShowPress(e: MotionEvent?) {
        Log.i(TAG, "onShowPress")
    }

    //用户（轻触触摸屏后）松开，由一个1个MotionEvent ACTION_UP触发
    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        Log.i(TAG, "onSingleTapUp")
        return true
    }

    /***************ScaleGestureDetector.OnScaleGestureListener****************/
    override fun onScale(detector: ScaleGestureDetector?): Boolean {
        Log.i(TAG, "onScale:\n detect = $detector")
        return true
    }

    override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
        Log.i(TAG, "onScaleBegin:\n detector = $detector")
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector?) {
        Log.i(TAG, "onScaleEnd")
    }
}