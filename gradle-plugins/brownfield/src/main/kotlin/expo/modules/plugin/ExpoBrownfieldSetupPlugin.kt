package expo.modules.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import com.android.build.gradle.LibraryExtension
import java.io.File

class ExpoBrownfieldSetupPlugin : Plugin<Project> {
  private lateinit var appProject: Project
  private lateinit var brownfieldProject: Project
  private lateinit var libraryExtension: LibraryExtension
  private lateinit var appBuildDir: File
  private lateinit var moduleBuildDir: File

  override fun apply(project: Project) {
    project.plugins.apply("maven-publish")
    project.evaluationDependsOn(":expo")

    project.afterEvaluate { project ->
      brownfieldProject = project
      libraryExtension = getLibraryExtension()
      appProject = findAppProject()

      appBuildDir = appProject.layout.buildDirectory.get().asFile
      moduleBuildDir = brownfieldProject.layout.buildDirectory.get().asFile

      setupSourceSets()
      setupCopyingAutolinking()
      setupTasks()
      setupCopyingNativeLibs()
    }
  }

  private fun setupSourceSets() {
    val main = libraryExtension.sourceSets.getByName("main")
    main.assets.srcDirs("$appBuildDir/generated/assets/createBundleReleaseJsAndAssets")
    main.res.srcDirs("$appBuildDir/generated/res/createBundleReleaseJsAndAssets")
    main.java.srcDirs("$moduleBuildDir/generated/autolinking/src/main/java")

    libraryExtension.sourceSets.getByName("release").apply {
      jniLibs.srcDirs("libsRelease")
    }
  
    libraryExtension.sourceSets.getByName("debug").apply {
      jniLibs.srcDirs("libsDebug")
    }
  }

  private fun setupCopyingAutolinking() {
    val path = "generated/autolinking/src/main/java"
    brownfieldProject.tasks.register("copyAutolinkingSources", Copy::class.java) { task ->
      task.dependsOn(":${appProject.name}:generateAutolinkingPackageList")
      task.from("$appBuildDir/$path")
      task.into("$moduleBuildDir/$path")

      val rnEntryPointTask = appProject.tasks.findByName("generateReactNativeEntryPoint")
      if (rnEntryPointTask != null) {
        task.dependsOn(rnEntryPointTask)
      }

      task.doLast {
        val sourceFile = File(moduleBuildDir, "$path/com/facebook/react/ReactNativeApplicationEntryPoint.java")
        if (sourceFile.exists()) {
            var content = sourceFile.readText()
            // TODO: Hardocded value
            val nameSpace = "com.pmleczek.expobrownfieldtargetexample.brownfield"
            val regex = Regex("""\b[\w.]+(?=\.BuildConfig)""")
            content = content.replace(regex, nameSpace)
            sourceFile.writeText(content)
        }
      }
    }
  }

  private fun setupTasks() {
    brownfieldProject.tasks.named("preBuild").configure { task ->
      task.dependsOn("copyAutolinkingSources")
    }

    // TODO: Release vs Debug?
    brownfieldProject.tasks.named("preBuild").configure { task ->
      task.dependsOn(appProject.tasks.named("createBundleReleaseJsAndAssets"))
    }
  }

  private fun setupCopyingNativeLibs() {
    // TODO: Add Debug handling
    val mergeJniLibsTask = brownfieldProject.tasks.named("mergeReleaseJniLibFolders")

    val stripTaskPath = ":${appProject.name}:stripReleaseDebugSymbols"
    val codegenTaskPath = ":${brownfieldProject.name}:generateCodegenSchemaFromJavaScript"

    val fromDir = appProject.layout.buildDirectory
      .dir("intermediates/stripped_native_libs/release/stripReleaseDebugSymbols/out/lib")
      .get().asFile
    val intoDir = brownfieldProject.rootProject.file("${brownfieldProject.name}/libsRelease")

    val copyTask = brownfieldProject.tasks.register("copyAppModulesLib", Copy::class.java) { task ->
      task.dependsOn(stripTaskPath, codegenTaskPath)
      task.from(fromDir)
      task.into(intoDir)
      task.include("**/libappmodules.so", "**/libreact_codegen_*.so")
    }

    mergeJniLibsTask.configure { task ->
      task.dependsOn(copyTask)
    }
  }

  private fun getLibraryExtension(): LibraryExtension {
    return brownfieldProject.extensions.getByType(LibraryExtension::class.java)
  }

  private fun findAppProject(): Project {
    return brownfieldProject.rootProject.subprojects.firstOrNull { it.plugins.hasPlugin("com.android.application") }
      ?: throw IllegalStateException("App project not found in the root project")
  }
}
