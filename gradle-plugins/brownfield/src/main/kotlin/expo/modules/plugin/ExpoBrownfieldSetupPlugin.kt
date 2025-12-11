package expo.modules.plugin

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import java.io.File

class ExpoBrownfieldSetupPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.evaluationDependsOn(":expo")
    project.afterEvaluate { project ->
      setupSourceSets(project)
      setupCopyingAutolinking(project)
      setupBundleDependencyForRelease(project)
      setupCopyingNativeLibsForType(project, "Release")
      setupCopyingNativeLibsForType(project, "Debug")
    }
  }

  /**
   * Setup the source sets for the project.
   *
   * @param brownfieldProject The brownfield project to setup the source sets for.
   */
  private fun setupSourceSets(
    brownfieldProject: Project
  ) {
    val libraryExtension = getLibraryExtension(brownfieldProject)
    val appProject = findAppProject(brownfieldProject)
    val appBuildDir = appProject.layout.buildDirectory.get().asFile
    val moduleBuildDir = brownfieldProject.layout.buildDirectory.get().asFile

    val main = libraryExtension.sourceSets.getByName("main")
    main.java.srcDirs("$moduleBuildDir/generated/autolinking/src/main/java")

    libraryExtension.sourceSets.getByName("release").apply {
      jniLibs.srcDirs("libsRelease")
      assets.srcDirs("$appBuildDir/generated/assets/createBundleReleaseJsAndAssets")
      res.srcDirs("$appBuildDir/generated/res/createBundleReleaseJsAndAssets")
    }

    libraryExtension.sourceSets.getByName("debug").apply {
      jniLibs.srcDirs("libsDebug")
    }
  }

  /**
   * Setup the copying of the autolinking sources.
   * 
   * The autolinking sources are copied from the app project to the brownfield project.
   *
   * @param brownfieldProject The brownfield project to setup the copying of the autolinking sources for.
   */
  private fun setupCopyingAutolinking(
    brownfieldProject: Project
  ) {
    val libraryExtension = getLibraryExtension(brownfieldProject)
    val appProject = findAppProject(brownfieldProject)

    val path = "generated/autolinking/src/main/java"
    val appBuildDir = appProject.layout.buildDirectory
    val moduleBuildDir = brownfieldProject.layout.buildDirectory

    val fromDir = appBuildDir.dir(path)
    val intoDir = moduleBuildDir.dir(path)

    brownfieldProject.tasks.register("copyAutolinkingSources", Copy::class.java) { task ->
      task.dependsOn(":${appProject.name}:generateAutolinkingPackageList")
      task.from(fromDir)
      task.into(intoDir)

      val rnEntryPointTask = appProject.tasks.findByName("generateReactNativeEntryPoint")
      if (rnEntryPointTask != null) {
        task.dependsOn(rnEntryPointTask)
      }

      task.doLast {
        val sourceFile = File(moduleBuildDir.get().asFile, "$path/com/facebook/react/ReactNativeApplicationEntryPoint.java")
        if (sourceFile.exists()) {
            var content = sourceFile.readText()
            val namespace = libraryExtension.namespace 
              ?: throw IllegalStateException("Namespace hasn't been configured for the library extension")
            val regex = Regex("""\b[\w.]+(?=\.BuildConfig)""")
            content = content.replace(regex, namespace)
            sourceFile.writeText(content)
        }
      }
    }

    brownfieldProject.tasks.named("preBuild").configure { task ->
      task.dependsOn("copyAutolinkingSources")
    }
  }

  /**
   * Setup the dependency of the bundle tasks.
   * 
   * Needed to include bundle and assets in the release variant.
   *
   * @param brownfieldProject The brownfield project to setup the dependency of the bundle tasks for.
   */
  internal fun setupBundleDependencyForRelease(
    brownfieldProject: Project
  ) {
    val appProject = findAppProject(brownfieldProject)
    brownfieldProject.tasks.named("preReleaseBuild").configure { task ->
      task.dependsOn(":${appProject.name}:createBundleReleaseJsAndAssets")
    }
  }

  /**
   * Setup the copying of the native libraries for a given build type.
   * 
   * The native libraries are copied from the app project to the brownfield project
   * 
   * @param brownfieldProject The brownfield project to setup the copying of the native libraries for.
   * @param buildType The build type to setup the copying of the native libraries for.
   */
  private fun setupCopyingNativeLibsForType(
    brownfieldProject: Project,
    buildType: String
  ) {
    val appProject = findAppProject(brownfieldProject)

    val mergeJniLibsTask = brownfieldProject.tasks.named("merge${buildType}JniLibFolders")

    val stripTaskPath = ":${appProject.name}:strip${buildType}DebugSymbols"
    val codegenTaskPath = ":${brownfieldProject.name}:generateCodegenSchemaFromJavaScript"

    val fromDir = appProject.layout.buildDirectory
      .dir("intermediates/stripped_native_libs/${buildType.toLowerCase()}/strip${buildType}DebugSymbols/out/lib")
    val intoDir = brownfieldProject.rootProject.file("${brownfieldProject.name}/libs${buildType}")

    val copyTask = brownfieldProject.tasks.register("copyAppModulesLib${buildType}", Copy::class.java) { task ->
      task.dependsOn(stripTaskPath, codegenTaskPath)
      task.from(fromDir)
      task.into(intoDir)
      task.include("**/libappmodules.so", "**/libreact_codegen_*.so")
    }

    mergeJniLibsTask.configure { task ->
      task.dependsOn(copyTask)
    }
  }

  /**
   * Get the library extension for the project.
   * 
   * @param project The project to get the library extension for.
   * @return The library extension for the project.
   * @throws DomainObjectNotFoundException if the library extension is not found.
   */
  private fun getLibraryExtension(project: Project): LibraryExtension {
    return project.extensions.getByType(LibraryExtension::class.java)
  }
}
