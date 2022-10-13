package com.example.bitmapdemo

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.drakeet.multitype.ItemViewBinder
import com.example.madslearning.databinding.ImageItemBinding

/**
 * 使用ItemViewBinder主要是为了处理在recycleView中multiType的情况，在本Demo中用不到
 * ItemViewBinder的第一个类型参数是数据源的数据结构，此处就是图片的url，String类型
 */
class ImageWallBinder : ItemViewBinder<String, BindingViewHolder<ImageItemBinding>>() {
    companion object{
        const val TAG = "ImageWallBinder"
    }
    private var urlList = mutableListOf<String>()

    fun setData(data: MutableList<String>) {
        urlList.clear()
        urlList.addAll(data)
        adapter.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): BindingViewHolder<ImageItemBinding> {
        return BindingViewHolder(ImageItemBinding.inflate(inflater, parent, false))
    }

    /**
     * 「问题一」需要考虑一个问题，如果用户快速上下滑动，会短期内造成多个异步任务，造成线程池的拥堵，如何解决?
     *
     * 「解决方法」尝试在列表划动的时候不加载图片，列表停止划动的时候才刷新？
     *
     * 「问题二」 因为在recycleView中有itemView复用的问题，所以当recycleview滑动的时候，后面出现的item会复用之前消失的item的view；
     *  - 「影响范围」举例子说：譬如view中有个icon，默认是gone的，在某个item数据源情况下被设置为visible，之后划动recycleview，后面的数据都是默认隐藏的，<br>
     *  但是没有重新设置icon的可见行，那么这时候会复用之前的view，展示为visible的状态。
     *
     * 在本Demo业务中的表现：
     * - 1.在本图片加载中也有类似的问题，新进入屏幕的item会复用之前的image；
     * - 2.由于image需要网络加载，所以可能当item1正在加载网络图片的过程中，用户已经将recycleview划走了，导致屏幕上当前应该是itemN，但是网络图片这时候才返回，
     * 会导致图片显示错乱的问题。（大概就是因为复用view，所以即使传递了imageView的引用也是不行的）
     *
     * 「解决方案」这里采用的解决方案是使用tag，将图片的url作为tag。
     * - 如果获取到imageView的tag为空，表示还没有开始复用，那么可以正常加载图片
     * - 如果获取到的tag非空且和当前的URL一致，表示这个view是复用的，而且需要加载的图片和复用的view的图片一致
     * - 如果获取到的tag非空且和当前的Url不一致，表示这个view是复用的，但是之前的view显示的图片和现在需要的图片不一致。
     */
    override fun onBindViewHolder(holder: BindingViewHolder<ImageItemBinding>, item: String) {
        with(holder.binding) {
            val tag : String? = myImage.tag as String?
            if (tag == null || item != tag) {
                Log.i(TAG, "tag = null or not same, origin tag = $tag, new tag = $item")
                //tag为空，或不一致，正常加载图片
                ImageLoader.getInstance(myImage.context).getBitmapAsync(myImage, item)
            } else {
                //tag一致，可以直接复用之前的view，不需要处理
                Log.i(TAG, "tag is the same: $item")
            }
        }

    }

    /**
     * 在调用notifyItemChanged(int position, @Nullable Object payload)方法的时候会调用该方法刷新
     */
    override fun onBindViewHolder(
        holder: BindingViewHolder<ImageItemBinding>,
        item: String,
        payloads: List<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, item)
        } else {
            //在这里处理payload非空情况下的业务
            super.onBindViewHolder(holder, item, payloads)
        }
    }

}