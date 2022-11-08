package com.example.bitmapdemo

import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.example.madslearning.R
import com.example.madslearning.customview.Helper
import com.example.madslearning.customview.enableScaleAndMove
import com.example.madslearning.databinding.FragmentClipImageBinding

/**
 * @Author lilong
 * @Date 2022/10/12
 * @Description
 */
class ClipImageFragment: Fragment() {

    companion object{
        const val TAG = "ClipImageFragment"
    }

    private lateinit var binding: FragmentClipImageBinding
    private val ratio = 0.5625//9:16

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentClipImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    val helper by lazy {  Helper(binding.ivOrigin) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivOrigin.enableScaleAndMove(true, true, helper)

        binding.tvTitle.setOnClickListener {
            BitmapUtils.drawableToBitmap(R.drawable.sample)?.let {
                binding.ivOrigin.setImageBitmap(
                    BitmapUtils.mergeBitmap(
                        it, BitmapUtils.waterMarkBitmap(
                            "hello",
                            30,
                            Color.parseColor("#FFFFFFFF"),
                            it.width,
                            it.height
                        )
                    )
                )
            }
            binding.ivOrigin.setImageResource(R.drawable.sample)
        }

        binding.ctlUsefulContainer.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener{
                override fun onGlobalLayout() {
                    val rect = Rect()
                    binding.ctlUsefulContainer.getHitRect(rect)
                    helper.updateMinRect(RectF(rect))
                    binding.ctlUsefulContainer.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        )

        binding.tvSend.setOnClickListener {
            BitmapUtils.drawableToBitmap(R.drawable.sample)?.let {
                binding.ivOrigin.setImageBitmap(it)
            }
//            binding.ivOrigin.setImageResource(R.drawable.honour_icon)
        }
    }




}