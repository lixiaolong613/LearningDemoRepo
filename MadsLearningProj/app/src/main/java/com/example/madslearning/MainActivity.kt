package com.example.madslearning

import android.app.usage.StorageStatsManager
import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.UserHandle
import android.os.storage.StorageManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bitmapdemo.CommonUtils
import com.example.madslearning.databinding.ActivityMainBinding
import com.example.madslearning.feature2.IFeature2Module
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.apache.lucene.util.RamUsageEstimator
import java.io.File
import java.math.BigDecimal
import java.util.ServiceLoader
import kotlin.collections.ArrayList


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
//        FullScreenUtils.enableDrawToSysUi(window, true, false)
//        FullScreenUtils.enableTransparentStatusBar(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onResume() {
        super.onResume()
        binding.rvFruit.adapter = FruitRecycleAdapter()
        binding.rvFruit.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        testFlow1()
        //开始动画
        binding.tv1.setOnClickListener(View.OnClickListener {

            if (true) {
                execModule()
            } else {
                printMemoryLog()

                initViewPager()
                testJVMMemory()
                //todo stateFlow这里在非主线程的情况下会有数据丢失？？？？？？？？？
                lifecycleScope.launch(Dispatchers.Main.immediate) {
                    numFlow.emit(2)
                    numFlow.emit(3)
                }
            }

        })
    }


    private fun execModule() {
        val module : ServiceLoader<IFeature2Module> = ServiceLoader.load(IFeature2Module::class.java)
        val it = module.iterator()
        while (it.hasNext()) {
            it.next().register()
        }

    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun printMemoryLog() {
        // 内部存储
        Log.i(TAG,"getCacheDir = ${CommonUtils.getContext().getCacheDir()}, mem = ${getFileMem(CommonUtils.getContext().getCacheDir())}") // /data/user/0/com.example.madslearning/cache
        Log.i(TAG,"getFilesDir = ${CommonUtils.getContext().getFilesDir()}, mem = ${getFileMem(CommonUtils.getContext().getFilesDir())}") // /data/user/0/com.example.madslearning/files
        Log.i(TAG,"codeCacheDir = ${CommonUtils.getContext().codeCacheDir}, mem = ${getFileMem(CommonUtils.getContext().codeCacheDir)}") // /data/user/0/com.example.madslearning/files

        //扩展外部存储
        Log.i(TAG,"getExternalFilesDir = ${CommonUtils.getContext().getExternalFilesDir(null)}, mem = ${getFileMem(CommonUtils.getContext().getExternalFilesDir(null))}") // /storage/emulated/0/Android/data/com.example.madslearning/files
        Log.i(TAG,"getExternalCacheDir = ${CommonUtils.getContext().getExternalCacheDir()}, mem = ${getFileMem(CommonUtils.getContext().getExternalCacheDir())}") // /storage/emulated/0/Android/data/com.example.madslearning/cache
        CommonUtils.getContext().getExternalFilesDirs(Environment.MEDIA_MOUNTED).forEach {
            Log.i(TAG,"getExternalFilesDirs = ${it.path}, mem = ${getFileMem(it)}") //
        }

        Log.i(TAG,"getExternalStorageState = ${Environment.getExternalStorageState()}")
        Log.i(TAG,"getExternalStorageDirectory = ${Environment.getExternalStorageDirectory()}, mem = ${getFileMem(Environment.getExternalStorageDirectory())}") // /storage/emulated/0
        //DIRECTORY_MUSIC  音乐目录
        //DIRECTORY_PICTURES  图片目录
        //DIRECTORY_MOVIES  电影目录
        //DIRECTORY_DOWNLOADS  下载目录
        //DIRECTORY_DCIM   相机拍照或录像文件的存储目录
        //DIRECTORY_DOCUMENTS   文件文档目录
        Log.i(TAG,"getExternalStoragePublicDirectory = ${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)}")// /storage/emulated/0/Documents

        Log.i(TAG,"dataDir = ${CommonUtils.getContext().dataDir}, mem = ${getFileMem(CommonUtils.getContext().dataDir)}") // /data/user/0/com.example.madslearning
        Log.i(TAG,"Environment.getDataDirectory = ${Environment.getDataDirectory()}, mem = ${getFileMem(Environment.getDataDirectory())}") // /

        getAppMem()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getAppMem() {
        val uid = applicationInfo.uid
        val storageManager = CommonUtils.getContext().getSystemService(StorageStatsManager::class.java)
        val storageStat = storageManager.queryStatsForPackage(StorageManager.UUID_DEFAULT, CommonUtils.getContext().packageName, UserHandle.getUserHandleForUid(uid))
        Log.i(TAG, "storageStat appBytes = ${storageStat.appBytes.byte2Kb()}")//应用程序的大小。这包括 APK 文件、优化的编译器输出和解压的原生库
        Log.i(TAG, "storageStat cacheBytes = ${storageStat.cacheBytes.byte2Kb()}")
        Log.i(TAG, "storageStat dataBytes = ${storageStat.dataBytes.byte2Kb()}")
    }

    private fun getFileMem(dirFile: File?): Int {
        if (dirFile?.exists() == true) {
            val files = dirFile.listFiles()
            files?.forEach {
                Log.i(TAG, "    ${it.path}: ${getFolderSize(it).byte2Kb()}")
            }
        }
        return 0
    }

    private fun getFolderSize(file: File) : Long {
        if (file.exists()) {
            return if (file.isDirectory) {
                var size = 0L
                file.listFiles()?.forEach {
                    size += getFolderSize(it)
                }
                size
            } else {
                file.length()
            }
        }
        return 0
    }

    private fun Long.byte2Kb(): Double {
        val size = this/(1024)
        val bigDecimal = BigDecimal(size)
        return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
    }

    val numFlow = MutableStateFlow<Int>(1)
    private fun testFlow1() {
        lifecycleScope.launch {
            numFlow.collect {
//                Log.i(TAG, "testFlow111 numFlow collect = $it")
            }
        }
    }

    private val flipperAdapter by lazy { FlipperAdapter() }
    private var flipperJob: Job? = null

    private fun initViewPager() {
        binding.vpFlipBanner.adapter = flipperAdapter.apply {
            updateData(mutableListOf(
                "1111111",
                "2222222",
                "3333333",
                "4444444",
                "5555555",
                "6666666"
            ))
        }
//        binding.vpFlipBanner.isUserInputEnabled = false
        startFlipper(lifecycleScope, 3000, 3000)
    }


    private fun testJVMMemory() {
        logMemory("Long", 1L)
        logMemory("Int", 1)
        logMemory("String", "aaaaaaaaa")
        logMemory("String class", String::class)
        logMemory("Fruit", Fruit())
        val array = ArrayList<Fruit?>(100000)
        logMemory("Array<Fruit>", array)
        logMemory("Array<Long>", ArrayList<Long>())
        logMemory("Array.class", ArrayList::class.java)
    }

    //占用内存大小
    private fun logMemory(type: String, obj: Any) {
        Log.i(TAG, "$type = ${RamUsageEstimator.shallowSizeOf(obj)}, sizeOf = ${RamUsageEstimator.sizeOf(obj)}")
    }

    //viewpager2自动无限轮播
    private fun startFlipper(scope: CoroutineScope, doAfterDelay: Long, step: Long, doOnRepeat: (() -> Unit)? = null) {
        if (flipperAdapter.itemCount > 1) {
            flipperJob?.cancel()
            flipperJob = scope.launch {
                delay(doAfterDelay)
                while (true) {
                    binding.vpFlipBanner.setCurrentItem(binding.vpFlipBanner.currentItem + 1, true)
                    if (binding.vpFlipBanner.currentItem == flipperAdapter.itemCount - 1) {
                        binding.vpFlipBanner.setCurrentItem(0, false)
                    }
                    delay(step)
                }
            }
        }
    }

    inner class Fruit() {
        private var size: Int? = null
        private var size2: Int? = null
        private var size3: Int? = null
        private var size4: Int? = null
    }
}