package com.example.madslearning.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.madslearning.databinding.FragmentViewpagerBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext


class ViewPagerFragment: Fragment() {
    companion object{
        const val TAG = "ViewPagerFragment"
    }
    private lateinit var binding: FragmentViewpagerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewpagerBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewpager.adapter = DemoViewPagerAdapter(childFragmentManager)
        binding.tabLayout.setupWithViewPager(binding.viewpager)
    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(EmptyCoroutineContext).launch {
            delay(1_000L)
            Log.i(TAG, "onResume, viewpager childCount = ${binding.viewpager.childCount}")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }



    internal class DemoViewPagerAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm) {
        override fun getCount() = 5

        override fun getItem(position: Int): Fragment {
            return ViewPagerContentFragment.makeInstance(position + 1)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return "OBJECT ${position + 1}"
        }
    }
}