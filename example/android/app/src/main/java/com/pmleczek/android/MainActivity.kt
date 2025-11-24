package com.pmleczek.android

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
import com.pmleczek.expobrownfieldtargetexample.brownfield.showReactNativeFragment

class MainActivity : AppCompatActivity(), DefaultHardwareBackBtnHandler {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        showReactNativeFragment()
    }

    override fun invokeDefaultOnBackPressed() {
        TODO("Not yet implemented")
    }
}
