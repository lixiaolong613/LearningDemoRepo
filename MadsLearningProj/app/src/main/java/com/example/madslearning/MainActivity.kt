package com.example.madslearning

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import com.example.madslearning.databinding.ActivityMainBinding
import com.example.madslearning.fragments.ViewPagerFragment

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.tvShowViewpager.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.tv_show_viewpager -> {
                supportFragmentManager.beginTransaction()
                    .add(R.id.fg_container, ViewPagerFragment::class.java, null)
                    .commit()
            }
        }
    }


}