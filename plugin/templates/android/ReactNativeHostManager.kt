package ${{packageId}}

import android.app.Application
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactPackage
import com.facebook.react.PackageList
import com.facebook.react.ReactApplication
import com.facebook.react.ReactHost
import com.facebook.react.common.ReleaseLevel
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint
import com.facebook.react.ReactNativeApplicationEntryPoint.loadReactNative
import com.facebook.react.defaults.DefaultReactNativeHost
import expo.modules.ApplicationLifecycleDispatcher
import expo.modules.ReactNativeHostWrapper

class ReactNativeHostManager {
    companion object {
        val shared: ReactNativeHostManager by lazy { ReactNativeHostManager() }
        private var reactNativeHost: ReactNativeHost? = null
        private var reactHost: ReactHost? = null
    }

    fun getReactNativeHost(): ReactNativeHost? {
        return reactNativeHost
    }

    fun getReactHost(): ReactHost? {
        return reactHost
    }

    fun initialize(application: Application) {
        if (reactNativeHost != null && reactHost != null) {
            return
        }

        DefaultNewArchitectureEntryPoint.releaseLevel = try {
          ReleaseLevel.valueOf(BuildConfig.REACT_NATIVE_RELEASE_LEVEL.uppercase())
        } catch (e: IllegalArgumentException) {
          ReleaseLevel.STABLE
        }
        loadReactNative(application)
        ApplicationLifecycleDispatcher.onApplicationCreate(application)

        val reactApp = object : ReactApplication {
            override val reactNativeHost: ReactNativeHost = ReactNativeHostWrapper(application,
                object : DefaultReactNativeHost(application) {
                   override fun getPackages(): List<ReactPackage> =
                      PackageList(this).packages.apply {}

                    override fun getJSMainModuleName(): String = ".expo/.virtual-metro-entry"

                    override fun getBundleAssetName(): String = "index.android.bundle"

                    override fun getUseDeveloperSupport(): Boolean = BuildConfig.DEBUG

                    override val isNewArchEnabled: Boolean = BuildConfig.IS_NEW_ARCHITECTURE_ENABLED
                })

            override val reactHost: ReactHost
                get() = ReactNativeHostWrapper.createReactHost(application.getApplicationContext(), reactNativeHost)
        }

        reactNativeHost = reactApp.reactNativeHost
        reactHost = reactApp.reactHost
    }
}
