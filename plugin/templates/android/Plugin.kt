package com.pmleczek.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.tasks.Copy
import org.gradle.api.artifacts.Dependency
import com.android.build.gradle.LibraryExtension
import org.gradle.api.internal.artifacts.dependencies.DefaultProjectDependency
import java.io.File
import java.nio.file.Paths
import expo.modules.plugin.ExpoGradleExtension
import expo.modules.plugin.configuration.GradleProject
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.Action

class ExpoBrownfieldPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.evaluationDependsOn(":expo")

        // TODO: Fix this more properly?
        val expoProject = project.rootProject.project(":expo")
        val releaseSourcesTask = expoProject.tasks.findByName("releaseSourcesJar")
        releaseSourcesTask?.dependsOn(expoProject.tasks.named("generatePackagesList"))

        setupConfigurations(project)
        setupSourceSets(project)
        
        project.afterEvaluate {
          processDependencies(project)
        }
    }

    /*
     * Setup configurations to be resolvable.
     * 
     * @param project The project to setup configurations for.
     */
    private fun setupConfigurations(project: Project) {
      val configurations = listOf("releaseImplementation", "releaseApi")
      configurations.forEach { configurationName ->
        val configuration = project.configurations.findByName(configurationName)
        if (configuration == null) {
          throw IllegalStateException("Configuration with name '$configurationName' not found")
        }
        configuration.setCanBeResolved(true)
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

    /*
     * Finds the app project in the root project.
     * 
     * @param root The root project
     * @return The app project
     */
    private fun findAppProject(root: Project): Project? {
      return root.allprojects.firstOrNull { it.plugins.hasPlugin("com.android.application") }
    }

    /*
     * Process dependencies.
     * 
     * @param project The project to process
     */
    private fun processDependencies(project: Project) {
        processExpoDependencies(project)
        processThirdPartyRNDependencies(project)
        processCoreRNDependencies(project)
    }

    /*
     * Returns Expo dependencies.
     * 
     * @param project The project to process
     * @return List of dependencies
     */
    private fun getExpoDependencies(project: Project): List<GradleProject> {
      val gradleExtension = project.gradle.extensions.findByType(ExpoGradleExtension::class.java)
        ?: throw IllegalStateException("`ExpoGradleExtension` not found. Please, make sure that `useExpoModules` was called in `settings.gradle`.")
      val config = gradleExtension.config
      return config.allProjects
    }

    /*
     * Process Expo dependencies.
     * 
     * @param project The project to process
     */
    private fun processExpoDependencies(project: Project) {
      val dependencies = getExpoDependencies(project)
      val (prebuiltDependencies, projectDependencies) = dependencies.partition { it.usePublication }
      projectDependencies.forEach { dependency ->
        val dependencyProject = project.rootProject.project(":${dependency.name}")
        applyOnce(dependencyProject, ExpoBrownfieldPublishPlugin::class.java)
      }

      prebuiltDependencies.forEach { dependency ->
        configurePublishing(project, dependency)
      }
    }

    /*
     * Returns third party RN dependencies (like e.g. react-native-screens).
     * This doesn't include Expo dependencies.
     * 
     * @param project The project to process
     * @return List of dependencies
     */
    private fun getThirdPartyRNDependencies(project: Project): List<DefaultProjectDependency> {
      val implementationConfiguration = project.configurations.findByName("implementation")
      val defaultDependencies = implementationConfiguration?.dependencies?.filterIsInstance<DefaultProjectDependency>()
      return defaultDependencies?.filter { it.name != "expo" } ?: emptyList()
    }

    /*
     * Process third party RN dependencies (like e.g. react-native-screens).
     * This doesn't cover Expo dependencies.
     * 
     * @param project The project to process
     */
    private fun processThirdPartyRNDependencies(project: Project) {
      val dependencies = getThirdPartyRNDependencies(project)
        dependencies.forEach { dependency ->
          val dependencyProject = project.rootProject.project(":${dependency.name}")
          applyOnce(dependencyProject, ExpoBrownfieldPublishPlugin::class.java)
        }
    }

    /*
     * Returns core RN artifacts (like e.g. react-android, hermes-android).
     * 
     * @param project The project to process
     * @return List of artifacts
     */
    private fun getCoreRNArtifacts(project: Project): List<ResolvedArtifact> {
      val configurations = listOf("releaseImplementation", "releaseApi")
      return configurations.flatMap { configurationName ->
        val configuration = project.configurations.findByName(configurationName)
        if (configuration == null) {
          return emptyList()
        }
        configuration.resolvedConfiguration.resolvedArtifacts.filter { it.moduleVersion.id.group.startsWith("com.facebook") }
      }
    }

    /*
     * Process core RN artifacts (like e.g. react-android, hermes-android).
     * 
     * @param project The project to process
     */
    private fun processCoreRNDependencies(project: Project) {
      val dependencies = getCoreRNArtifacts(project)
      dependencies.forEach { dependency ->
        configurePublishing(project, dependency)
      }
    }

    /*
     *  Applies a plugin to a project.
     * If the plugin is already applied, it will not be applied again.
     * 
     * @param project The project to process
     * @param plugin The plugin to apply
     */
    private fun applyOnce(project: Project, plugin: Class<out Plugin<Project>>) {
      if (!project.plugins.hasPlugin(plugin)) {
        project.plugins.apply(plugin)
      }
    }

    private fun configurePublishing(project: Project, dependency: GradleProject) {
      var publishing = project.extensions.getByType(PublishingExtension::class.java)

      val aarFile = File(makeAarPath(project, dependency))
      if (!aarFile.exists()) {
        throw IllegalStateException("AAR file not found: ${aarFile.absolutePath}")
      }

      val publicationName = "mavenAar-${dependency.publication?.artifactId}-${dependency.publication?.version}"
      publishing.publications.create(publicationName, MavenPublication::class.java) {
        groupId = dependency.publication?.groupId
        artifactId = dependency.publication?.artifactId
        version = dependency.publication?.version

        artifact(aarFile) {
          extension = "aar"
        }
      }

      publishing.repositories{
        mavenLocal()
      }

      project.gradle.projectsEvaluated {
          val publishTask = project.tasks.findByName("publish${publicationName.capitalize()}PublicationToMavenLocal")
          if (publishTask != null) {
              project.tasks.named("assembleRelease").configure {
                  finalizedBy(publishTask)
              }
          }
      }
    }

    private fun configurePublishing(project: Project, dependency: ResolvedArtifact) {
      val publishing = project.extensions.getByType(PublishingExtension::class.java)

      val aarFile = File(dependency.file.absolutePath)
      if (!aarFile.exists()) {
        throw IllegalStateException("AAR file not found: ${aarFile.absolutePath}")
      }

      val publicationName = "mavenAar-${dependency.moduleVersion.id.name}-${dependency.moduleVersion.id.version}"
      if (publishing.publications.findByName(publicationName) == null) {
        publishing.publications.create(publicationName, MavenPublication::class.java) {
          groupId = dependency.moduleVersion.id.group
          artifactId = dependency.moduleVersion.id.name
          version = dependency.moduleVersion.id.version

          artifact(aarFile) {
            extension = "aar"
          }
        }
      }

      publishing.repositories{
        mavenLocal()
      }

      project.gradle.projectsEvaluated {
        val publishTask = project.tasks.findByName("publish${publicationName.capitalize()}PublicationToMavenLocal")
        if (publishTask != null) {
            project.tasks.named("assembleRelease").configure {
                finalizedBy(publishTask)
            }
        }
      }
    }

    private fun makeAarPath(project: Project, dependency: GradleProject): String {
      return Paths.get(
          project.rootDir.absolutePath,
          "..",
          "node_modules",
          dependency.name,
          dependency.publication?.repository,
          dependency.publication?.groupId?.replace(".", "/"),
          dependency.publication?.artifactId,
          dependency.publication?.version,
          "${dependency.publication?.artifactId}-${dependency.publication?.version}.aar"
      ).normalize().toString()
  }
}
