package com.example.demofeature2

import android.util.Log
import com.example.madslearning.feature2.IFeature2Module

private const val TAG = "Feature2Module"
class Feature2Module: IFeature2Module {
    override fun register() {
        Log.i(TAG, "registered")
    }
}