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
        
        project.afterEvaluate {
            listEmbedDependencies(project)
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

    private fun listEmbedDependencies(project: Project) {
        println("========================================")
        println("Listing dependencies for: ${project.name}")
        println("========================================")

        val config =  project.configurations.findByName("implementation")
        val defaultDependencies = config?.dependencies?.filterIsInstance<DefaultProjectDependency>()
        println("    - Default Dependencies:")
        defaultDependencies?.forEach { dependency ->
            println("    - ${dependency.group}:${dependency.name}:${dependency.version}")
            println("      Type: ${dependency::class.simpleName}")
        }
        println("========================================")

        println("    - Expo Dependencies:")
        val expoProject = project.rootProject.project("expo")
        val expoConfig = expoProject.configurations.findByName("api")
        expoConfig?.dependencies?.forEach { dependency ->
            println("    - ${dependency.group}:${dependency.name}:${dependency.version}")
            println("      Type: ${dependency::class.simpleName}")
        }
        println("========================================")

        // Try to resolve from api and implementation configurations
        val configsToCheck = listOf("api", "implementation", "releaseApi", "releaseImplementation", "runtimeClasspath", "compileClasspath")
        
        configsToCheck.forEach { configName ->
            val config = project.configurations.findByName(configName)
            if (config != null) {
                println("\n--- Configuration: $configName ---")
                config.setCanBeResolved(true)
                
                println("\n  Unresolved Dependencies (${config.dependencies.size}):")
                config.dependencies.forEach { dependency ->
                    println("    - ${dependency.group}:${dependency.name}:${dependency.version}")
                    println("      Type: ${dependency::class.simpleName}")
                }
                
                println("\n  Resolving...")
                try {
                    config.resolve()
                    val resolvedArtifacts = config.resolvedConfiguration.resolvedArtifacts
                    
                    println("  Resolved Artifacts (${resolvedArtifacts.size} total):")
                    resolvedArtifacts.forEachIndexed { index, artifact ->
                        println("\n    [${index + 1}] ${artifact.name}")
                        println("      Type: ${artifact.type}")
                        println("      Group: ${artifact.moduleVersion.id.group}")
                        println("      Version: ${artifact.moduleVersion.id.version}")
                        println("      File: ${artifact.file.absolutePath}")
                        println("      File exists: ${artifact.file.exists()}")
                        if (artifact.file.exists()) {
                            println("      File size: ${artifact.file.length()} bytes")
                        }
                    }
                    
                    // Summary by type
                    val byType = resolvedArtifacts.groupBy { it.type }
                    println("\n  Summary by Type:")
                    byType.forEach { (type, artifacts) ->
                        println("    $type: ${artifacts.size} artifact(s)")
                    }
                    
                } catch (e: Exception) {
                    println("  ERROR resolving $configName: ${e.message}")
                }
            } else {
                println("\n--- Configuration: $configName (not found) ---")
            }
        }
        
        println("\n========================================")
    }
}
