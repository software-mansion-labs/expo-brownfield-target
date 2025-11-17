package com.pmleczek.android

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
import com.pmleczek.expobrownfieldtargetexample.brownfield.ReactNativeHostManager

class MainActivity : AppCompatActivity(), DefaultHardwareBackBtnHandler {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ReactNativeHostManager.shared.initialize(this.application)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
    }

    override fun invokeDefaultOnBackPressed() {
        TODO("Not yet implemented")
    }
}
