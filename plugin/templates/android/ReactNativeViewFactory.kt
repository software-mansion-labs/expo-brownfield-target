package com.pmleczek.expobrownfieldtargetexample.brownfield

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import com.facebook.react.ReactRootView

/**
 * Factory for creating React Native views that can be embedded in native Android views.
 */
object ReactNativeViewFactory {
    
    /**
     * Creates a React Native view.
     * 
     * @param context The Android context
     * @param moduleName The name of the React Native component to render (e.g., "App")
     * @param initialProps Optional initial properties to pass to the React component
     * @return A ViewGroup containing the React Native view
     */
    fun createView(
        context: Context,
        moduleName: String = "main",
        initialProps: Bundle? = null
    ): ViewGroup {
        if (!ReactNativeHostManager.isInitialized()) {
            throw IllegalStateException(
                "ReactNativeHostManager must be initialized before creating views. " +
                "Call ReactNativeHostManager.initialize(application) in your Application.onCreate()"
            )
        }
        
        return ReactRootView(context).apply {
            startReactApplication(
                ReactNativeHostManager.reactNativeHost.reactInstanceManager,
                moduleName,
                initialProps
            )
        }
    }
    
    /**
     * Creates a FrameLayout containing a React Native view.
     * Useful for embedding in Fragments.
     * 
     * @param context The Android context
     * @param activity The Activity (needed for React Native)
     * @param moduleName The name of the React Native component to render
     * @param initialProps Optional initial properties to pass to the React component
     * @return A FrameLayout containing the React Native view
     */
    fun createFrameLayout(
        context: Context,
        activity: Activity,
        moduleName: String = "main",
        initialProps: Bundle? = null
    ): FrameLayout {
        return FrameLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            addView(createView(context, moduleName, initialProps))
        }
    }
    
    /**
     * Helper method to create a Bundle from a Map of properties.
     */
    fun createProps(props: Map<String, Any>): Bundle {
        return Bundle().apply {
            props.forEach { (key, value) ->
                when (value) {
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Double -> putDouble(key, value)
                    is Float -> putFloat(key, value)
                    is Boolean -> putBoolean(key, value)
                    is Long -> putLong(key, value)
                    else -> putString(key, value.toString())
                }
            }
        }
    }
}