package com.pmleczek.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
import com.pmleczek.android.ui.theme.AndroidTheme
import com.pmleczek.expobrownfieldtargetexample.brownfield.ReactNativeHostManager
import com.pmleczek.expobrownfieldtargetexample.brownfield.ReactNativeViewFactory

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
