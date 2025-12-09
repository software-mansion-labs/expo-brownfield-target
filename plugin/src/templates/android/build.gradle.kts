import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import com.android.build.gradle.LibraryExtension
import java.io.File

// group = "com.pmleczek.expobrownfieldtargetexample"
// version = "0.0.1"

plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
  id("com.facebook.react")
  id("expo-brownfield-setup")
}

react {
  autolinkLibrariesWithApp()
}

android {
    // TODO: Hardocded value
  namespace = "com.pmleczek.expobrownfieldtargetexample.brownfield"
  compileSdk = 36

  buildFeatures {
    buildConfig = true
  }

  defaultConfig {
    minSdk = 24
    consumerProguardFiles("consumer-rules.pro")
    buildConfigField("boolean", "IS_NEW_ARCHITECTURE_ENABLED", properties["newArchEnabled"].toString())
    buildConfigField("boolean", "IS_HERMES_ENABLED", properties["hermesEnabled"].toString())
    buildConfigField("boolean", "IS_EDGE_TO_EDGE_ENABLED", "false")
    buildConfigField("String", "REACT_NATIVE_RELEASE_LEVEL", "\"${findProperty("reactNativeReleaseLevel") ?: "stable"}\"")
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  kotlinOptions {
    jvmTarget = "17"
  }
}

dependencies {
  api("com.facebook.react:react-android:0.81.5")
  api("com.facebook.react:hermes-android:0.81.5")
  compileOnly("androidx.fragment:fragment-ktx:1.6.1")
}
