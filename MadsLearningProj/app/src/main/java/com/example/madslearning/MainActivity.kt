package com.example.madslearning

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.MultiTypeAdapter
import com.example.bitmapdemo.ClipImageFragment
import com.example.bitmapdemo.CustomItemDecorator
import com.example.bitmapdemo.ImageWallBinder
import com.example.bitmapdemo.NetworkUtils
import com.example.madslearning.databinding.ActivityMainBinding
import com.example.madslearning.fragment.GestureDetectFragment

private const val TAG = "Demo - TAG"
@RequiresApi(Build.VERSION_CODES.M)
class MainActivity : AppCompatActivity(), View.OnClickListener {

    val imageUrl_fresco = "https://giftesx.bigo.sg/live/3s4/2XyiFf.png"
    val imageUrl_fresco_wepb = "https://giftesx.bigo.sg/live/3s4/2XyiFf.png?resize=21&dw=80"

    private val urlList : MutableList<String>
        get() = mutableListOf(
            "http://e.hiphotos.baidu.com/image/pic/item/a1ec08fa513d2697e542494057fbb2fb4316d81e.jpg",
        )

    private val adapter: MultiTypeAdapter by lazy { MultiTypeAdapter() }

    private val showImageListener by lazy {
        object : ShowImageWhenNotWIFIListener{
            override fun onConfirm() {
                /**
                 * todo 在使用MultiTypeAdapter时，如果对数据源进行了更改，需要手动调用[RecyclerView.Adapter.notifyDataSetChanged]
                 *      后期可以对MultiTypeAdapter进行封装。属于recycleView类目的内容，在其他工程中优化
                 */
                adapter.apply {
                    items = urlList
                    notifyDataSetChanged()
                }
            }

            override fun onCancel() {
                //do nothing
            }
        }
    }

    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.tvTryOther.setOnClickListener {
            /**
             * 以m为单位,该值是硬件设备的限制，针对每个app的最大内存
             */
//            val heapGrowthLimit =
//                (applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager)?.memoryClass
//            Toast.makeText(this, "$heapGrowthLimit", Toast.LENGTH_SHORT).show()
            /**
             * 利用fresco加载图片
             */
            binding.frescoImage.setImageURI(imageUrl_fresco)
            binding.frescoImage2.setImageURI(imageUrl_fresco_wepb)
        }
        binding.rvMyImageWall.layoutManager = GridLayoutManager(this, 3)
//            binding.rvMyImageWall.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.rvMyImageWall.addItemDecoration(CustomItemDecorator())
        binding.rvMyImageWall.adapter = adapter.apply {
            register(ImageWallBinder())
        }
        binding.rvMyImageWall.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when(newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        //空闲状态。此时列表没有划动，允许刷新数据

                    }
                    else -> {
                        //列表划动，不允许划动

                    }
                }
            }
        })

        binding.tvMytv.setOnClickListener {
//            iv_star.setImageBitmap(mergeBitmapWithMaskColor(drawableToBitmap(R.drawable.daily_task_star),0x66000000))
            checkWifi(showImageListener)
            binding.rvMyImageWall.visibility = View.VISIBLE
        }
        binding.btn1.setOnClickListener(this)
        binding.btn2.setOnClickListener(this)
    }

    private fun checkWifi(listener: ShowImageWhenNotWIFIListener?) {
        val res = NetworkUtils.isWifiAvailable()
        Log.i(TAG, "isWifiAvailable = $res")
        if (!res) {
            val builder = AlertDialog.Builder(this).apply {
                setMessage(R.string.not_wifi_warning)
                setTitle(R.string.attention)
                setPositiveButton(R.string.confirm
                ) { dialog, which -> listener?.onConfirm() }
                setNegativeButton(R.string.cancel
                ) { _, _ -> listener?.onCancel() }
            }
            builder.show()
        } else {
            listener?.onConfirm()
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn1 -> {
                binding.rvMyImageWall.visibility = View.GONE
                if (supportFragmentManager.findFragmentByTag(ClipImageFragment.TAG) != null) {
                    return
                }
                val transac = supportFragmentManager.beginTransaction()
                transac.setReorderingAllowed(true)
                transac.add(R.id.fg_container, ClipImageFragment::class.java, null, ClipImageFragment.TAG)
                transac.commit()
            }
            R.id.btn2 -> {
                binding.rvMyImageWall.visibility = View.GONE
                if (supportFragmentManager.findFragmentByTag(GestureDetectFragment.TAG) != null) {
                    return
                }
                val transac = supportFragmentManager.beginTransaction()
                transac.setReorderingAllowed(true)
                transac.add(R.id.fg_container, GestureDetectFragment::class.java, null, GestureDetectFragment.TAG)
                transac.commit()
            }
        }
    }

    interface ShowImageWhenNotWIFIListener{
        fun onConfirm()
        fun onCancel()
    }
}