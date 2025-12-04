import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import com.android.build.gradle.LibraryExtension
import java.io.File

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.facebook.react")
    `maven-publish`
}

evaluationDependsOn(":expo")

react {
    autolinkLibrariesWithApp()
}

android {
    namespace = "com.pmleczek.testapp.brownfield"
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

    publishing {
      multipleVariants("release") {
        includeBuildTypeValues("debug", "release")
        withSourcesJar()
      }
    }
}

dependencies {
   debugApi("com.facebook.react:react-android:0.81.5") {
        artifact {
            classifier = "debug"
            type = "aar"
        }
    }
  
    // api("com.facebook.react:react-android:0.81.5")
    // api("com.facebook.react:hermes-android:0.81.5")
    
    debugApi("com.facebook.react:hermes-android:0.81.5") {
        artifact {
            classifier = "debug"
            type = "aar"
        }
    }
    
    releaseApi("com.facebook.react:react-android:0.81.5") {
        artifact {
            classifier = "release"
            type = "aar"
        }
    }
    
    releaseApi("com.facebook.react:hermes-android:0.81.5") {
        artifact {
            classifier = "release"
            type = "aar"
        }
    }

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

afterEvaluate {
  val androidExtension = extensions.getByType(LibraryExtension::class.java)

  // Find the main app module
  val appProject = rootProject.subprojects.firstOrNull { it.plugins.hasPlugin("com.android.application") }
    ?: throw IllegalStateException("App project not found")

  val appBuildDir = appProject.layout.buildDirectory.get().asFile
  val moduleBuildDir = layout.buildDirectory.get().asFile

  // --- Configure source sets ---

  val main = androidExtension.sourceSets.getByName("main")
  main.assets.srcDirs("$appBuildDir/generated/assets/createBundleReleaseJsAndAssets")
  main.res.srcDirs("$appBuildDir/generated/res/createBundleReleaseJsAndAssets")
  main.java.srcDirs("$moduleBuildDir/generated/autolinking/src/main/java")

  androidExtension.sourceSets.getByName("release").apply {
    jniLibs.srcDirs("libsRelease")
  }

  androidExtension.sourceSets.getByName("debug").apply {
    jniLibs.srcDirs("libsDebug")
  }

  tasks.register("copyAutolinkingSources", Copy::class) {
    val path = "generated/autolinking/src/main/java"

    dependsOn(":${appProject.name}:generateAutolinkingPackageList")

    from("$appBuildDir/$path")
    into("$moduleBuildDir/$path")

    // If you need to patch entry point:
    val rnEntryPointTask = appProject.tasks.findByName("generateReactNativeEntryPoint")
    if (rnEntryPointTask != null) {
        dependsOn(rnEntryPointTask)
    }

    doLast {
        val sourceFile = File(moduleBuildDir, "$path/com/facebook/react/ReactNativeApplicationEntryPoint.java")
        if (sourceFile.exists()) {
            var content = sourceFile.readText()
            val nameSpace = "com.pmleczek.testapp.brownfield"

            val regex = Regex("""\b[\w.]+(?=\.BuildConfig)""")
            content = content.replace(regex, nameSpace)

            sourceFile.writeText(content)
        }
    }
  }

  tasks.named("preBuild").configure {
    dependsOn("copyAutolinkingSources")
  }

  tasks.named("preBuild").configure {
    dependsOn(appProject.tasks.named("createBundleReleaseJsAndAssets"))
  }

  val mergeJniLibsTask = tasks.named("mergeReleaseJniLibFolders")

  val stripTaskPath = ":${appProject.name}:stripReleaseDebugSymbols"
  val codegenTaskPath = ":$name:generateCodegenSchemaFromJavaScript"

  val fromDir = appProject.layout.buildDirectory
      .dir("intermediates/stripped_native_libs/release/stripReleaseDebugSymbols/out/lib")
      .get().asFile

  val intoDir = rootProject.file("$name/libsRelease")

  val copyTask = tasks.register("copyAppModulesLib", Copy::class) {
      dependsOn(stripTaskPath, codegenTaskPath)
      from(fromDir)
      into(intoDir)
      include("**/libappmodules.so", "**/libreact_codegen_*.so")
  }

  mergeJniLibsTask.configure {
      dependsOn(copyTask)
  }
}

publishing {
    publications {
        create<MavenPublication>("mavenAar") {
            groupId = "com.pmleczek.testapp"
            artifactId = "brownfield"
            version = "0.0.1-local"
            afterEvaluate {
                from(components.getByName("release"))
            }
        }
    }

    repositories {
      maven {
        name = "customLocal"
        url = uri("file://${rootProject.projectDir.parentFile}/maven")
      }
    }
}
