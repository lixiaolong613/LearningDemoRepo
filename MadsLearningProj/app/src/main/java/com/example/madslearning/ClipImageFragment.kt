package com.example.bitmapdemo

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.ivOrigin.updateBitmap(BitmapUtils.drawableToBitmap(R.drawable.sample))
//        binding.ivOrigin.updateUsefulRect(Rect(binding.ctlUsefulContainer.left, binding.ctlUsefulContainer.top, binding.ctlUsefulContainer.right, binding.ctlUsefulContainer.bottom))
//        binding.ivOrigin.setOnClickListener {
//            Log.i(TAG, "image click")
//        }
    }





}