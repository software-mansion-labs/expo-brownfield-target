package com.pmleczek.android

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
import com.pmleczek.expobrownfieldtargetexample.brownfield.showReactNativeFragment
import expo.modules.brownfield.BrownfieldMessaging
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ReactNativeActivity : AppCompatActivity(), DefaultHardwareBackBtnHandler {
  private var listenerId: String? = null
  private val handler = Handler(Looper.getMainLooper())
  @RequiresApi(Build.VERSION_CODES.O)
  private val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss")
  private val sendMessageRunnable =
      object : Runnable {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun run() {
          val message =
              mapOf<String, Any?>(
                  "sender" to "Android app",
                  "receiver" to "React Native app",
                  "data" to
                      mapOf<String, Any?>(
                          "array" to
                              listOf<Any>(
                                  1,
                                  2,
                                  3.5,
                                  true,
                                  "hello",
                                  false,
                                  mapOf<String, String>("key" to "value"),
                              ),
                          "object" to
                              mapOf<String, Any?>(
                                  "nested" to mapOf<String, Any?>("key" to "value")
                              ),
                          "number" to 123.456,
                          "boolean" to false,
                          "string" to "hello",
                      ),
                  "metadata" to
                      mapOf<String, Any?>(
                          "timestamp" to LocalDateTime.now().format(formatter),
                          "platform" to "Android",
                      ),
              )
          BrownfieldMessaging.sendMessage(message)
          handler.postDelayed(this, 5_000)
        }
      }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    showReactNativeFragment()

    listenerId =
        BrownfieldMessaging.addListener { message ->
          println("Message from React Native received:")
          println(message)
        }
    handler.post(sendMessageRunnable)
  }

  override fun onDestroy() {
    super.onDestroy()

    listenerId?.let { BrownfieldMessaging.removeListener(it) }
    handler.removeCallbacks(sendMessageRunnable)
  }

  override fun invokeDefaultOnBackPressed() {
    TODO("Not yet implemented")
  }
}
