import groovy.json.JsonOutput
import groovy.json.JsonSlurper

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.facebook.react")
    `maven-publish`
    id("com.pmleczek.expo-brownfield")
}

react {
    autolinkLibrariesWithApp()
}

android {
    namespace = "com.pmleczek.expobrownfieldtargetexample.brownfield"
    compileSdk = 36

    buildFeatures {
      buildConfig = true
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        buildConfigField("boolean", "IS_NEW_ARCHITECTURE_ENABLED", properties["newArchEnabled"].toString())
        buildConfigField("boolean", "IS_HERMES_ENABLED", properties["hermesEnabled"].toString())
        // TODO: Examine this var in more detail
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

    packaging {
      jniLibs {
        pickFirsts += "lib/armeabi-v7a/libworklets.so"
        pickFirsts += "lib/arm64-v8a/libworklets.so"
        pickFirsts += "lib/x86/libworklets.so"
        pickFirsts += "lib/x86_64/libworklets.so"
      }
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    api("com.facebook.react:react-android:0.81.5")
    api("com.facebook.react:hermes-android:0.81.5")
    
    api("commons-io:commons-io:2.6")
    api("com.github.bumptech.glide:glide:4.16.0")
    api("com.github.bumptech.glide:avif-integration:4.16.0")
    api("com.github.bumptech.glide:okhttp3-integration:4.11.0")
    api("com.github.penfeizhou.android.animation:glide-plugin:3.0.5")
    api("com.caverock:androidsvg-aar:1.4")

    // We need to explicitly include Coil 
    // because of issues related to it in Screens
    val COIL_VERSION = "3.0.4"
    implementation("io.coil-kt.coil3:coil:${COIL_VERSION}")
    implementation("io.coil-kt.coil3:coil-network-okhttp:${COIL_VERSION}")
    implementation("io.coil-kt.coil3:coil-svg:${COIL_VERSION}")
}

publishing {
    publications {
        create<MavenPublication>("mavenAar") {
            groupId = "com.pmleczek.expobrownfieldtargetexample"
            artifactId = "brownfield"
            version = "0.0.1-local"
            afterEvaluate {
                from(components.getByName("release"))
            }

            pom {
                withXml {
                }
            }
        }
    }

    repositories {
        mavenLocal() // Publishes to the local Maven repository (~/.m2/repository by default)
    }
}

afterEvaluate {
  listOf("mergeReleaseJniLibFolders", "mergeDebugJniLibFolders").forEach { taskName ->
    tasks.named(taskName) {
      doFirst {
        // Remove duplicate libworklets.so from react-native-worklets to avoid conflicts
        // with react-native-reanimated which also provides the same library
        fileTree("$buildDir/intermediates/exploded-aar/expo-brownfield-target-example/react-native-worklets") {
          include("**/jni/**/libworklets.so")
        }.forEach { file ->
          println("Removing duplicate libworklets.so: ${file.path}")
          file.delete()
        }
      }
    }
  }
}
