package com.pmleczek.android

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
import com.pmleczek.expobrownfieldtargetexample.brownfield.ReactNativeHostManager
import com.pmleczek.expobrownfieldtargetexample.brownfield.ReactNativeFragment

class MainActivity : AppCompatActivity(), DefaultHardwareBackBtnHandler {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ReactNativeHostManager.shared.initialize(this.application)
        enableEdgeToEdge()

        val rnFragment = ReactNativeFragment.createFragmentHost(
            activity = this,
        )
        setContentView(rnFragment)
    }

    override fun invokeDefaultOnBackPressed() {
        TODO("Not yet implemented")
    }
}
