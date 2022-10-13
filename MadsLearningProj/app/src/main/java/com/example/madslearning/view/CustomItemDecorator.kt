package com.example.bitmapdemo

import android.graphics.*
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.madslearning.R

/**
 * 自定义recycleView装饰器
 * 1.首先通过getItemOffsets加边距
 * 2.利用onDraw函数，在这个距离上进行绘图
 * 在整个绘制流程中，是先调用ItemDecoration的[getItemOffsets]和[onDraw]函数，然后再调用Item的onDraw函数，最后调用ItemDecoration的[onDrawOver]函数。
 * 所以如果在ItemDecoration的onDraw中画了内容超出了outRect区域，最终是会被RecycleView的item覆盖的
 */
class CustomItemDecorator: RecyclerView.ItemDecoration() {

    private val paint: Paint = Paint().apply {
        color = Color.GREEN
    }

    private val bitmap = ImageResizer.decodeBitmapFromRes(CommonUtils.getResources(), R.drawable.honour_icon, 10.dp, 10.dp)

    /**
     * @param c 指通过getItemOffsets撑开的空白区域所对应的画布，通过这个canvas对象，可以在getItemOffsets所撑出来的区域任意绘图。
     * 需要注意的是,getItemOffsets是针对每个Item都会走一次，也就是说每个Item的outRect都可以不同，
     * 但是onDraw和onDrawOver并不是针对每个Item都会走一次，所以我们需要在onDraw和onDrawOver中绘图时，一次性将所有Item的ItemDecoration绘制完成,所以需要遍历所有item
     */
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val layout = parent.layoutManager
        layout?.let {
            for (i in 0 until parent.childCount) {
                val child = parent.getChildAt(i)
                val left = it.getLeftDecorationWidth(child) //这里就可以获取在getItemOffsets方法中给outRect设置的上下左右空间
                val cy = child.top + child.height / 2f
                val cx = child.left - left / 2f //child.left 是child本身在recycleView中的位置，是包括了decorator的
                c.drawCircle(cx, cy, 15f ,paint)
            }
        }

    }

    /**
     * onDrawOver 是绘制在最上层的，所以它的绘制位置并不受限制（当然，Decoration 的 onDraw 绘制范围也不受限制，只不过不可见，被Item所覆盖）
     */
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        val layout = parent.layoutManager
        layout?.let {
            for (i in 0 until parent.childCount) {
                val child = parent.getChildAt(i)
                bitmap?.let {
                    val index = parent.getChildAdapterPosition(child)
                    if (index % 5 == 0) {
                        c.drawBitmap(it, child.left - bitmap.width / 2f, child.top.toFloat(), paint)
                    }
                }
            }
//            parent.getChildAt(0)?.let {
//                val gradientDrawable = LinearGradient(
//                    parent.width / 2f,
//                    0f,
//                    parent.width / 2f,
//                    it.height * 2f,
//                    0xff0000ff.toInt(),
//                    0x000000ff,
//                    Shader.TileMode.CLAMP
//                )
//                paint.shader = gradientDrawable
//                c.drawRect(0f, 0f, parent.width.toFloat(), it.height * 2f, paint)
//            }
        }

    }

    /**
     * 主要作用就是给item的四周加上边距，实现的效果类似于margin，将item的四周撑开一些距离
     * @param outRect 表示在item的上下左右所撑开的距离。这个值默认是0
     * @param view 指当前Item的View对象
     * @param parent 指RecyclerView 本身
     * @param state 通过State可以获取当前RecyclerView的状态，也可以通过State在RecyclerView各组件间传递参数
     */
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
//        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = 5.dp
        outRect.left = 20.dp

//        outRect.right = 5.dp
    }
}