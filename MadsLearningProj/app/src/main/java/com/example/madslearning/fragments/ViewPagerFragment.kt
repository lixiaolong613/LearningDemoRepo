package com.example.madslearning.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.madslearning.databinding.FragmentViewpagerBinding


class ViewPagerFragment: Fragment() {

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


    internal class DemoViewPagerAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm) {
        override fun getCount() = 100

        override fun getItem(position: Int): Fragment {
            return ViewPagerContentFragment.makeInstance(position)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return "OBJECT ${position + 1}"
        }
    }
}