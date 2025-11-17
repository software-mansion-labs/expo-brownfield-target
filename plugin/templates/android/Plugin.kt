package com.pmleczek.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import com.android.build.gradle.LibraryExtension
import org.gradle.api.internal.artifacts.dependencies.DefaultProjectDependency
import java.io.File

class ExpoBrownfieldPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.evaluationDependsOn(":expo")
        setupSourceSets(project)
        setupConfigurations(project)
        project.afterEvaluate {
          afterEvaluate(project)
        }
    }

    private fun setupSourceSets(project: Project) {
      val androidExtension = project.extensions.getByType(LibraryExtension::class.java)
      val appProject = findAppProject(project.rootProject)
      if (appProject == null) {
        throw IllegalStateException("App project not found")
      }

      val appBuildDir = appProject.layout.buildDirectory.get().asFile
      val moduleBuildDir = project.layout.buildDirectory.get().asFile

      configureSourceSets(androidExtension, appBuildDir, moduleBuildDir)
      configureTasks(project, appProject, appBuildDir, moduleBuildDir)
    }

    private fun configureSourceSets(androidExtension: LibraryExtension, appBuildDir: File, moduleBuildDir: File) {
      val mainSourceSet = androidExtension.sourceSets.getByName("main")
      mainSourceSet.assets.srcDirs("$appBuildDir/generated/assets/createBundleReleaseJsAndAssets")
      mainSourceSet.res.srcDirs("$appBuildDir/generated/res/createBundleReleaseJsAndAssets")
      mainSourceSet.java.srcDirs("$moduleBuildDir/generated/autolinking/src/main/java")

      val releaseSourceSet = androidExtension.sourceSets.getByName("release")
      releaseSourceSet.jniLibs.srcDirs("libsRelease")

      val debugSourceSet = androidExtension.sourceSets.getByName("debug")
      debugSourceSet.jniLibs.srcDirs("libsDebug")
    }

    private fun getLibraryNameSpace(project: Project): String {
        val androidExtension = project.extensions.getByType(LibraryExtension::class.java)
        val nameSpace = androidExtension.namespace
        return nameSpace ?: throw IllegalStateException("namespace must be defined in your android library build.gradle")
    }

    private fun patchRNEntryPoint(task: Copy, path: String, project: Project, appProject: Project, moduleBuildDir: File) {
      val rnEntryPointTaskName = "generateReactNativeEntryPoint"
      val rnEntryPointTask = appProject.tasks.findByName(rnEntryPointTaskName) ?: return

      task.dependsOn(rnEntryPointTask)
      val sourceFile = File(moduleBuildDir, "$path/com/facebook/react/ReactNativeApplicationEntryPoint.java")
      task.doLast {
        if (sourceFile.exists()) {
          var content = sourceFile.readText()
          val nameSpace = getLibraryNameSpace(project)

          val regex = Regex("""\b[\w.]+(?=\.BuildConfig)""")
          content = content.replace(regex, nameSpace)
          sourceFile.writeText(content)
        }
      }
    }

    private fun configureTasks(project: Project, appProject: Project, appBuildDir: File, moduleBuildDir: File) {
      val appProjectName = appProject.name

      project.tasks.register("copyAutolinkingSources", Copy::class.java) {
        val path = "generated/autolinking/src/main/java"
        dependsOn(":$appProjectName:generateAutolinkingPackageList")
        from("$appBuildDir/$path")
        into("$moduleBuildDir/$path")

        patchRNEntryPoint(this, path, project, appProject, moduleBuildDir)
      }

      project.tasks.named("preBuild").configure {
          dependsOn("copyAutolinkingSources")
      }
    }

    private fun findAppProject(root: Project): Project? {
      return root.allprojects.firstOrNull { it.plugins.hasPlugin("com.android.application") }
    }


    private fun createConfiguration(project: Project, configName: String) {
        val configuration = project.configurations.create(configName)
        configuration.isVisible = false
        configuration.isTransitive = false
        // project.gradle.addListener(CustomDependencyResolver(project, configuration))
        // Configuration is automatically added to project.configurations when created
    }

    private fun setupConfigurations(project: Project) {
      createConfiguration(project, "embed")
      val androidExtension = project.extensions.getByType(LibraryExtension::class.java)
      androidExtension.buildTypes.all {
        createConfiguration(project, "${name}Embed")
      }
      androidExtension.productFlavors.all {
        val flavorName = name
        createConfiguration(project, "${flavorName}Embed")
        androidExtension.buildTypes.all {
          val variantName = "${flavorName}${name.replaceFirstChar(Char::titlecase)}Embed"
          createConfiguration(project, variantName)
        }
      }
    }

    private fun embedExpoDependencies(project: Project) {
      val expoProject = project.rootProject.project("expo")
      val expoConfig = expoProject.configurations.findByName("api")
      expoConfig?.dependencies?.forEach { dependency ->
        // TODO: Move it to some constants (?)
        if (true) {
          if (dependency is DefaultProjectDependency) {
            project.dependencies.add("embed", expoProject.dependencies.project(mapOf("path" to ":${dependency.name}")))
          } else {
            project.dependencies.add("embed", dependency)
          }
        }
      }
    } 
    private fun processArtifacts(project: Project) {
      embedExpoDependencies(project)
    }

    private fun afterEvaluate(project: Project) {
      println("--------------------------------")
      println("After evaluate: ${project.name}")
      processArtifacts(project)
      // TODO: Maybe set transitive to true for all configurations?
      println("--------------------------------")
    }
}
