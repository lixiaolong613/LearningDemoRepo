package com.example.madslearning.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.madslearning.R
import com.example.madslearning.databinding.FragmentViewpagerContentBinding
import com.example.madslearning.databinding.RecyclerViewItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.EmptyCoroutineContext

@SuppressLint("LongLogTag")
class ViewPagerContentFragment: Fragment() {

    companion object{
        const val TAG = "ViewPagerContentFragment"
        const val KEY_INDEX = "index"

        fun makeInstance(index: Int) = ViewPagerContentFragment().apply {
            arguments = bundleOf(KEY_INDEX to index)
        }
    }

    private lateinit var binding: FragmentViewpagerContentBinding
    private val index
        get() = arguments?.getInt(KEY_INDEX)?:-1


    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i(TAG, "onAttach for fragment $index")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate for fragment $index")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewpagerContentBinding.inflate(inflater, container, false)
        Log.i(TAG, "onCreateView for fragment $index")
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.i(TAG, "onActivityCreated for fragment $index")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated for fragment $index")
        binding.tvContent.text = "index_$index"
    }

    //region lifecycle function
    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart for fragment $index")
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume for fragment $index")
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause for fragment $index")
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop for fragment $index")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i(TAG, "onDestroyView for fragment $index")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy for fragment $index")
    }

    override fun onDetach() {
        super.onDetach()
        Log.i(TAG, "onDetach for fragment $index")
    }

    //endregion

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        Log.i(TAG, "setUserVisibleHint for fragment $index, isVisibleToUser = $isVisibleToUser")
    }
}