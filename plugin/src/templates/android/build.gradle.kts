import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import com.android.build.gradle.LibraryExtension
import java.io.File

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

        // TODO: Maybe remove?
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    // TODO: Maybe use 'default' instead of 'release'?
    publishing {
      multipleVariants("brownfieldDebug") {
        includeBuildTypeValues("debug")
        withSourcesJar()
      }

      multipleVariants("brownfieldRelease") {
        includeBuildTypeValues("release")
        withSourcesJar()
      }

      multipleVariants("brownfieldAll") {
        includeBuildTypeValues("debug", "release")
        withSourcesJar()
      }
    }
}

dependencies {
    api("com.facebook.react:react-android:0.81.5")
    api("com.facebook.react:hermes-android:0.81.5")

    compileOnly("androidx.fragment:fragment-ktx:1.6.1")

    api("org.jetbrains.kotlin:kotlin-reflect:2.0.21")
    api("androidx.browser:browser:1.6.0")
    api("commons-io:commons-io:2.6")
    api("com.github.bumptech.glide:glide:4.16.0")
    api("com.github.bumptech.glide:avif-integration:4.16.0")
    api("com.github.bumptech.glide:okhttp3-integration:4.11.0")
    api("com.github.penfeizhou.android.animation:glide-plugin:3.0.5")
    api("com.caverock:androidsvg-aar:1.4")

    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.12.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}

publishing {
    publications {
        create<MavenPublication>("brownfieldDebug") {
            // TODO: Hardocded values
            groupId = "com.pmleczek.expobrownfieldtargetexample"
            // TODO: Hardocded value
            artifactId = "brownfield"
            version = "0.0.1-local"
            afterEvaluate {
                from(components.getByName("brownfieldDebug"))
            }
        }

        create<MavenPublication>("brownfieldRelease") {
          // TODO: Hardocded values
          groupId = "com.pmleczek.expobrownfieldtargetexample"
          // TODO: Hardocded value
          artifactId = "brownfield"
          version = "0.0.1-local"
          afterEvaluate {
              from(components.getByName("brownfieldRelease"))
          }
        }

        create<MavenPublication>("brownfieldAll") {
          // TODO: Hardocded values
          groupId = "com.pmleczek.expobrownfieldtargetexample"
          // TODO: Hardocded value
          artifactId = "brownfield"
          version = "0.0.1-local"
          afterEvaluate {
            from(components.getByName("brownfieldAll"))
          }
        }
    }

    repositories {
      // TODO: Hardocded value
      maven {
        name = "customLocal"
        url = uri("file://${rootProject.projectDir.parentFile}/maven")
      }
    }
}
