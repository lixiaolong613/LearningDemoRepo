package com.example.madslearning

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.example.madslearning.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object{
        const val TAG = "MainActivityLog"
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
    }
}