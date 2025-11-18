import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import expo.modules.plugin.ExpoGradleExtension

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.facebook.react")
    id("com.callstack.react.brownfield")
    `maven-publish`
}

reactBrownfield {
    isExpo = true
}

react {
    autolinkLibrariesWithApp()
}

android {
    namespace = "${{packageId}}"
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

val gradleExtension = project.gradle.extensions.findByType(ExpoGradleExtension::class.java)
    ?: throw IllegalStateException("`ExpoGradleExtension` not found. Please, make sure that `useExpoModules` was called in `settings.gradle`.")
val config = gradleExtension.config
val (prebuiltProjects, projects) = config.allProjects.partition { it.usePublication }


dependencies {
   debugApi("com.facebook.react:react-android:0.81.5") {
        artifact {
            classifier = "debug"
            type = "aar"
        }
    }
    
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

    api("org.jetbrains.kotlin:kotlin-reflect:2.0.21")
    api("androidx.browser:browser:1.6.0")
    api("commons-io:commons-io:2.6")
    api("com.github.bumptech.glide:glide:4.16.0")
    api("com.github.bumptech.glide:avif-integration:4.16.0")
    api("com.github.bumptech.glide:okhttp3-integration:4.11.0")
    api("com.github.penfeizhou.android.animation:glide-plugin:3.0.5")
    api("com.caverock:androidsvg-aar:1.4")

    // Embed the subproject Expo packages
    projects.forEach { proj ->
      embed(project(":${proj.name}"))
    }

    // Embed the prebuilt Expo packages
    prebuiltProjects.forEach { proj ->
      val publication = requireNotNull(proj.publication)
      embed("${publication.groupId}:${publication.artifactId}:${publication.version}")
    }

    // We need to explicitly include Coil 
    // because of issues related to it in Screens
    val COIL_VERSION = "3.0.4"
    implementation("io.coil-kt.coil3:coil:${COIL_VERSION}")
    implementation("io.coil-kt.coil3:coil-network-okhttp:${COIL_VERSION}")
    implementation("io.coil-kt.coil3:coil-svg:${COIL_VERSION}")

    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.12.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}

fun isExpoDep(group: String, artifactId: String): Boolean {
    return group == "host.exp.exponent" && artifactId == "expo"
}

publishing {
    publications {
        create<MavenPublication>("mavenAar") {
            groupId = "${{groupId}}"
            artifactId = "${{artifactId}}"
            version = "0.0.1-local"
            afterEvaluate {
                from(components.getByName("release"))
            }

            pom {
                withXml {
                    val dependenciesNode = (asNode().get("dependencies") as groovy.util.NodeList).first() as groovy.util.Node
                    dependenciesNode.children()
                        .filterIsInstance<groovy.util.Node>()
                        .filter {
                            val artifactId = (it["artifactId"] as groovy.util.NodeList).text()
                            val group = (it["groupId"] as groovy.util.NodeList).text()

                            (isExpoDep(group, artifactId) || group == rootProject.name)
                        }
                        .forEach { dependenciesNode.remove(it) }
                }
            }
        }
    }

    repositories {
        mavenLocal()
    }
}

val moduleBuildDir: Directory = layout.buildDirectory.get()

tasks.register("removeDependenciesFromModuleFile") {
    doLast {
        file("$moduleBuildDir/publications/mavenAar/module.json").run {
            val json = inputStream().use { JsonSlurper().parse(it) as Map<String, Any> }
            (json["variants"] as? List<MutableMap<String, Any>>)?.forEach { variant ->
                (variant["dependencies"] as? MutableList<Map<String, Any>>)?.removeAll {
                    val module = it["module"] as String
                    val group = it["group"] as String

                    (isExpoDep(group, module) || group == rootProject.name)
                }
            }
            writer().use { it.write(JsonOutput.prettyPrint(JsonOutput.toJson(json))) }
        }
    }
}

tasks.named("generateMetadataFileForMavenAarPublication") {
    finalizedBy("removeDependenciesFromModuleFile")
}

afterEvaluate {
  listOf("mergeReleaseJniLibFolders", "mergeDebugJniLibFolders").forEach { taskName ->
    tasks.named(taskName) {
      doFirst {
        fileTree("$buildDir/intermediates/exploded-aar/${rootProject.name}/react-native-worklets") {
          include("**/jni/**/libworklets.so")
        }.forEach { file ->
          println("Removing duplicate libworklets.so: ${file.path}")
          file.delete()
        }
      }
    }
  }
}
