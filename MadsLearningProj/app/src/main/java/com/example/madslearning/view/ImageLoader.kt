package com.example.bitmapdemo

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.util.LruCache
import android.widget.ImageView
import androidx.annotation.Nullable
import com.example.madslearning.R
import com.jakewharton.disklrucache.DiskLruCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest


/**
 * 需要提供的功能
 * - 同步加载
 * - 异步加载
 * - 图片压缩
 * - 内存缓存
 * - 磁盘缓存
 * - 网络拉取
 */
@SuppressLint("UsableSpace")
class ImageLoader(mContext: Context) {
    companion object {
        const val TAG = "ImageLoader"
        const val MESSAGE_POST_RESULT = 1
        val DISK_CACHE_SIZE by lazy {
            1024L * 1024 * 50//50MB
        }
        const val DISK_CACHE_INDEX = 0
        const val IMAGE_SIZE = 8192//8KB
        private val REQ_WIDTH = 100.dp
        private val REQ_HEIGHT = 100.dp
        const val TAG_KEY_URI = 1

        @Volatile
        private var instance: ImageLoader? = null
        fun getInstance(mContext: Context): ImageLoader {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = ImageLoader(mContext)
                    }
                }
            }
            return instance!!
        }
    }

    private var mMemoryCache: LruCache<String, Bitmap>? = null
    private var mDiskLruCache: DiskLruCache? = null
    private val mMainHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MESSAGE_POST_RESULT -> {
                    val info = msg.obj as? LoaderResult
                    info?.let {
                        val imageview: ImageView = it.imageView
                        val url = it.url
                        if (TextUtils.equals(url, imageview.getTag(R.id.url) as? String)) {
                            try {
                                imageview.setImageBitmap(it.bitmap)
                            } catch (e: Exception) {
                                Log.i(TAG, "setImageBitmap Error! ${e.printStackTrace()}")
                            }
                        } else {
                            Log.w(TAG, "Set Image bitmap, but url has changed, ignored!")
                        }
                    }
                }
                else -> {

                }
            }
        }
    }

    init {
        val cacheSize = (Runtime.getRuntime().maxMemory() / 1024).toInt()  //KB
        mMemoryCache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String?, value: Bitmap?): Int {
                return ((value?.rowBytes)?.times((value.height)) ?: 0) / 1024
            }
        }

        val diskCacheDir = getCacheFile(mContext)
        if (!diskCacheDir.exists()) {
            diskCacheDir.mkdirs()
        }
        //需要先判断一下File对象的可用空间是否大于要申请的磁盘缓存空间！usableSpace是非root用户的，freeSpace是root用户的
        if (diskCacheDir.usableSpace > DISK_CACHE_SIZE) {
            try {
                //第一个参数是缓存目录
                //第二个参数是应用的版本号，当但本好发生改变时，会清空之前的所有缓存文件，【一般为1】
                //第三个参数表示单个节点对应的数据的个数【同一个key可以对应多少个文件，一般为1】
                //第四个参数表示总缓存的大小
                mDiskLruCache = DiskLruCache.open(diskCacheDir, 1, 1, DISK_CACHE_SIZE)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @Nullable
    fun getBitmapFromResources(res: Resources, id: Int): Bitmap? {
        return ImageResizer.decodeBitmapFromRes(res, id, REQ_WIDTH, REQ_HEIGHT)
    }

    /**
     * 同步加载图片，【会阻塞调用线程，因为可能需要网络加载】
     */
    @Nullable
    fun getBitmapSync(url: String): Bitmap? {
        var res: Bitmap? = null
        val key = getMD5Str(url)
        //先从内存缓存中寻找
        res = loadBitmapFromMemory(url)
        if (res != null) {
            return res
        }
        //再从磁盘缓存中寻找
        res = loadBitmapFromDisk(url)
        if (res != null) {
            putBitmap2Memory(key, res)
            return res
        }
        //最后才从网络加载图片
        res = getBitmapFromNet(url)
        putBitmap2Memory(key, res)
        return res
    }

    /**
     * 异步加载图片【从内存缓存中读取image还是同步的】
     */
    fun getBitmapAsync(imageView: ImageView, url: String) {
        imageView.setTag(R.id.url, url)//key必须是id资源
        //先从内存缓存中寻找
        val res: Bitmap? = loadBitmapFromMemory(url)
        if (res != null) {
            imageView.setImageBitmap(res)
        }
        //用协程的方式
        try {
            getBitmapFromDiskAndNetAsync(imageView, url)
        } catch (e: NullPointerException) {
            Log.i(TAG, "execute Error!!! ${e.printStackTrace()}")
        }

//用线程的方式
//        val runnable = Runnable {
//            res = getBitmapSync(url)
//            if (res != null) {
//                val mes = LoaderResult(imageView, url, res!!)
//                mMainHandler.obtainMessage(MESSAGE_POST_RESULT, mes)
//                    .sendToTarget()
//            }
//        }
//        try {
//            AppExecutors.get().execute(TaskType.NETWORK, runnable)
//        } catch (e: NullPointerException) {
//            Log.i(TAG, "execute Error!!! ${e.printStackTrace()}")
//        }
    }

    /**
     * 协程方式从磁盘或者网络加载图片
     */
    private fun getBitmapFromDiskAndNetAsync(imageView: ImageView, url: String) {
        val key = getMD5Str(url)
        var res: Bitmap?
        val job = CoroutineScope(Dispatchers.IO).launch {
            //先从磁盘缓存中寻找
            res = loadBitmapFromDisk(url)
            if (res != null) {
                putBitmap2Memory(key, res)
            } else {
                //再从网络加载图片
                res = getBitmapFromNet(url)
                putBitmap2Memory(key, res)
            }
            if (res != null) {
                val mes = LoaderResult(imageView, url, res!!)
                mMainHandler.obtainMessage(MESSAGE_POST_RESULT, mes)
                    .sendToTarget()
            }
        }
    }


    /**
     * 从内存缓存中加载bitmap
     */
    private fun loadBitmapFromMemory(url: String): Bitmap? {
        var res: Bitmap? = null
        val key = getMD5Str(url)
        //先从内存缓存中寻找
        mMemoryCache?.let {
            res = it.get(key)
            res?.let {
                return it
            }
        }
        return res
    }

    /**
     * 从磁盘中加载图片，并添加到内存缓存中
     */
    private fun loadBitmapFromDisk(url: String): Bitmap? {
        val key = getMD5Str(url)
        try {
            val snapShot = mDiskLruCache?.get(key)
            snapShot?.let {
                val fileInputStream = it.getInputStream(DISK_CACHE_INDEX) as? FileInputStream
                fileInputStream?.let { file ->
                    val fd = file.fd
                    return ImageResizer.decodeBitmapFromFileDescriptor(fd, REQ_WIDTH, REQ_HEIGHT)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * 从网络加载图片，并将图片添加到磁盘缓存和内存缓存
     * - 不能在主线程执行，
     */
    private fun getBitmapFromNet(url: String): Bitmap? {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw RuntimeException("can not visit network from UI thread")
        }
        val key = getMD5Str(url)
        mDiskLruCache?.let { disk ->
            try {
                val editor = disk.edit(key)
                editor?.let {
                    val outputStream = it.newOutputStream(DISK_CACHE_INDEX)
                    if (downloadImage(url, outputStream)) {
                        editor.commit()
                    } else {
                        editor.abort()
                    }
                    mDiskLruCache?.flush()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }

        }
        return loadBitmapFromDisk(url)
    }

    private fun downloadImage(url: String, outputStream: OutputStream): Boolean {
        var urlConnection: HttpURLConnection? = null
        var outStream: BufferedOutputStream? = null
        var inStream: BufferedInputStream? = null
        try {
            //网络请求这一步本身是同步的，会阻塞调用线程
            val mUrl = URL(url)
            urlConnection = mUrl.openConnection() as HttpURLConnection
            inStream = BufferedInputStream(urlConnection.inputStream, IMAGE_SIZE)
            outStream = BufferedOutputStream(outputStream, IMAGE_SIZE)
            var b: Int
            while (inStream.read().also { b = it } != -1) {
                outStream.write(b)
            }
            return true
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            urlConnection?.disconnect()
            IOUtils.closeQuietly(inStream)
            IOUtils.closeQuietly(outStream)
        }
        return false
    }

    /**
     * 将bitmap放到内存缓存，并返回bitmap本身
     */
    private fun putBitmap2Memory(key: String, bitmap: Bitmap?) {
        bitmap?.let { bm ->
            mMemoryCache?.put(key, bm)
        }
    }

    /**
     * 获取图片缓存目录
     */
    private fun getCacheFile(context: Context): File {
        var dataDir: String
        // 获取缓存目录
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() || !Environment.isExternalStorageRemovable()) {
            dataDir = context.getExternalFilesDir(null)?.path ?: context.filesDir.path
        } else {
            dataDir = context.filesDir.path
        }
        val tmp = context.cacheDir.absolutePath
        Log.i(
            TAG,
            "cacheDir = $tmp, dataDir = ${context.getExternalFilesDir(null)?.path}, filesPath = ${context.filesDir.path}"
        )
        return File(dataDir + File.separator + "ImageCache")
    }

    /**
     * 将url做MD5转换
     */
    private fun getMD5Str(url: String): String {
        val mDigest = MessageDigest.getInstance("MD5")
        mDigest.update(url.toByteArray())
        return bytes2HexString(mDigest.digest())
    }

    private fun bytes2HexString(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (i in 0 until bytes.count()) {
            val hex = Integer.toHexString(0xFF and bytes[i].toInt())
            if (hex.length <= 1) {
                sb.append('0')
            }
            sb.append(hex)
        }
        return sb.toString()
    }

    data class LoaderResult(val imageView: ImageView, val url: String, val bitmap: Bitmap)
}