package com.example.madslearning.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.madslearning.databinding.FragmentViewpagerContentBinding


class ViewPagerContentFragment: Fragment() {

    companion object{
        const val TAG = "ViewPagerContentFragment"
        const val KEY_INDEX = "index"

        fun makeInstance(index: Int) = ViewPagerContentFragment().apply {
            arguments = bundleOf(KEY_INDEX to index)
        }
    }

    private lateinit var binding: FragmentViewpagerContentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewpagerContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getInt(KEY_INDEX)?.let {
            binding.tvContent.text = "index_$it"
        }
    }
}