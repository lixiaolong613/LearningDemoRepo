package com.example.madslearning

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
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

private const val TAG = "Demo - TAG"
@RequiresApi(Build.VERSION_CODES.M)
class MainActivity : AppCompatActivity(), View.OnClickListener {

    val imageUrl_fresco = "https://giftesx.bigo.sg/live/3s4/2XyiFf.png"
    val imageUrl_fresco_wepb = "https://giftesx.bigo.sg/live/3s4/2XyiFf.png?resize=21&dw=80"

    private val urlList : MutableList<String>
        get() = mutableListOf(
            "http://e.hiphotos.baidu.com/image/pic/item/a1ec08fa513d2697e542494057fbb2fb4316d81e.jpg",
//            "https://iconfont.alicdn.com/t/2a6a9c21-6b0d-4a8e-ba46-90390f968627.png",
//            "https://iconfont.alicdn.com/t/5104a4dc-6e1c-449b-a759-63e690f9e144.png",
//            "https://iconfont.alicdn.com/t/2d20e6a8-211b-474e-bb3a-6c5c1e4ea7cf.png",
//            "https://iconfont.alicdn.com/t/0fa61035-9555-483e-92c8-3abeb89d139a.png",
//            "https://iconfont.alicdn.com/t/c9a8dac7-1ae3-4d4d-8ee5-c6b24a75afe9.png",
//            "https://iconfont.alicdn.com/t/4305a185-bd9c-4286-bb44-209acaf7ad7d.png",
//            "https://iconfont.alicdn.com/t/534acfb1-1331-4c83-a6c0-4186ae8ad4e5.png",
//            "https://iconfont.alicdn.com/t/685a561e-1280-442e-be19-14bdb5645d3d.png",
//            "https://iconfont.alicdn.com/t/0e8317a4-bd97-4e67-af1e-d97b10bb0b0a.png",
//            "https://iconfont.alicdn.com/t/ebb69aac-ea30-4260-ad3c-0b726cd2439d.png",
//            "https://iconfont.alicdn.com/t/150a70a4-7e01-4d18-af40-09827e902260.png",
//            "https://iconfont.alicdn.com/t/5a06b4e6-9cfc-4dd5-a786-dfe0622e92fc.png",
//            "https://iconfont.alicdn.com/t/7b8b3fbd-10ba-478c-bdde-031526f4e853.png",
//            "https://iconfont.alicdn.com/t/743e3163-a3f4-4aef-82cc-23247b2e870e.png",
//            "https://iconfont.alicdn.com/t/424e4f01-0510-4de6-b079-6617cf25cc2d.png",
//            "https://iconfont.alicdn.com/t/8911ec1d-8b45-4c62-aacc-2f83308ac87c.png",
//            "https://iconfont.alicdn.com/t/394156db-a6ee-4568-893c-6ebe9b71c2b3.png",
//            "https://iconfont.alicdn.com/t/a77891bf-b36d-4647-b83b-07548b536ba5.png",
//            "https://iconfont.alicdn.com/t/fc034ff1-20fe-4af4-bb69-945e1acec99e.png",
//            "https://iconfont.alicdn.com/t/30d6dfc8-e9a6-434f-baed-2ce55971687c.png",
//            "https://iconfont.alicdn.com/t/08384701-db9a-446a-97cf-b6bf6aae16ba.png",
//            "https://iconfont.alicdn.com/t/3b9bef53-2e0e-40c2-a41a-1e879979ae1d.png",
//            "https://iconfont.alicdn.com/t/fc513afc-23f6-4d13-a112-c40a59508060.png",
//            "https://iconfont.alicdn.com/t/fc513afc-23f6-4d13-a112-c40a59508060.png",
//            "https://iconfont.alicdn.com/t/f5371a6b-56f1-440e-ad14-8aabcf14b284.png",
//            "https://iconfont.alicdn.com/t/c2b8956b-c4e2-4a81-a245-e1f3713cdd5c.png",
//            "https://iconfont.alicdn.com/t/0e5eda8b-e53c-4659-9062-5a2dc82a45e6.png",
//            "https://iconfont.alicdn.com/t/2172a269-5f21-4e6d-bcb6-e68137ab43fc.png",
//            "https://iconfont.alicdn.com/t/a36e8455-1350-40fb-af19-ba6c661d3817.png",
//            "https://iconfont.alicdn.com/t/b8253ed6-5142-44c8-9b28-97fcfecb213d.png"
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
        }
    }

    interface ShowImageWhenNotWIFIListener{
        fun onConfirm()
        fun onCancel()
    }
}