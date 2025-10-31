package com.pmleczek.expobrownfieldtargetexample.brownfield

import android.app.Application
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactPackage
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.load
import com.facebook.react.defaults.DefaultReactNativeHost
import com.facebook.soloader.SoLoader
import expo.modules.ApplicationLifecycleDispatcher
import expo.modules.ReactNativeHostWrapper

/**
 * Manages the React Native host for brownfield integration.
 * Initializes React Native with Expo modules support.
 */
object ReactNativeHostManager {
    
    private var _reactNativeHost: ReactNativeHost? = null
    
    val reactNativeHost: ReactNativeHost
        get() = _reactNativeHost 
            ?: throw IllegalStateException("ReactNativeHostManager must be initialized first")
    
    /**
     * Initialize React Native with Expo modules. Call this once during app startup.
     * 
     * @param application The Application instance
     */
    fun initialize(application: Application) {
        if (_reactNativeHost != null) {
            return // Already initialized
        }
        
        SoLoader.init(application, false)
        
        // Register Expo lifecycle events
        ApplicationLifecycleDispatcher.onApplicationCreate(application)
        
        // Create ReactNativeHost wrapped with Expo support
        val innerHost = object : DefaultReactNativeHost(application) {
            override fun getPackages(): List<ReactPackage> {
                // Return empty list - packages will be autolinked when this module
                // is used in a host app that has proper autolinking configured
                return emptyList()
            }

            override fun getJSMainModuleName(): String {
                return ".expo/.virtual-metro-entry"
            }

            override fun getUseDeveloperSupport(): Boolean {
                return BuildConfig.DEBUG
            }

            override val isNewArchEnabled: Boolean
                get() = BuildConfig.IS_NEW_ARCHITECTURE_ENABLED
                
            override val isHermesEnabled: Boolean
                get() = BuildConfig.IS_HERMES_ENABLED
        }
        
        // Wrap with Expo's ReactNativeHostWrapper for Expo module support
        _reactNativeHost = ReactNativeHostWrapper(application, innerHost)
        
        if (BuildConfig.IS_NEW_ARCHITECTURE_ENABLED) {
            // If you opted-in for the New Architecture, we load the native entry point for this app.
            load()
        }
    }
    
    /**
     * Check if React Native has been initialized.
     */
    fun isInitialized(): Boolean = _reactNativeHost != null
}