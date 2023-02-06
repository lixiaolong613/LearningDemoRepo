package com.example.madslearning

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madslearning.databinding.ActivityMainBinding
import com.example.madslearning.utils.FullScreenUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    companion object{
        const val TAG = "MainActivityLog"
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
//        window.decorView.systemUiVisibility =
//                // Tells the system that the window wishes the content to
//                // be laid out at the most extreme scenario. See the docs for
//                // more information on the specifics
//            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
//                    // Tells the system that the window wishes the content to
//                    // be laid out as if the navigation bar was hidden
//                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        FullScreenUtils.enableDrawToSysUi(window, true, false)
        FullScreenUtils.enableTransparentStatusBar(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        binding.rvFruit.adapter = FruitRecycleAdapter()
        binding.rvFruit.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        testFlow()
        //开始动画
        binding.tv1.setOnClickListener(View.OnClickListener {
            //todo 这里在非主线程的情况下会有数据丢失？？？？？？？？？
            lifecycleScope.launch(Dispatchers.IO) {
                numFlow.tryEmit(2)
//                delay(100)
                numFlow.tryEmit(3)
            }
//            numFlow.tryEmit(2)
//            numFlow.tryEmit(3)


        })
    }
    val numFlow = MutableStateFlow<Int>(0)

    private fun testFlow() {
        lifecycleScope.launch {
            numFlow.collect {
                Log.i(TAG, "numFlow collect = $it")
            }
        }
    }
}