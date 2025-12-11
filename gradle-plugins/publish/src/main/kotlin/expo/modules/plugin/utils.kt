package expo.modules.plugin

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import expo.modules.plugin.ExpoGradleExtension
import expo.modules.plugin.configuration.GradleProject
import groovy.json.JsonSlurper
import groovy.util.Node
import org.gradle.api.XmlProvider
import java.io.File

internal fun findAppProject(project: Project): Project {
  val appProject = project.rootProject.subprojects.firstOrNull { it.plugins.hasPlugin("com.android.application") }
    ?: throw IllegalStateException("App project not found in the root project")
  return appProject
}

internal fun getBrownfieldProject(rootProject: Project, libraryName: String): Project {
  val brownfieldProject = rootProject.project(":${libraryName}")
    ?: throw IllegalStateException("Brownfield project with name \"${libraryName}\" not found in the root project")
  return brownfieldProject
}

internal fun getExpoPrebuiltProjects(rootProject: Project): List<GradleProject> {
  val gradleExtension = rootProject.gradle.extensions.findByType(ExpoGradleExtension::class.java)
      ?: throw IllegalStateException("`ExpoGradleExtension` not found. Please, make sure that `useExpoModules` was called in `settings.gradle`.")
  val config = gradleExtension.config
  val projects = config.allProjects.filter { it.usePublication }
  
  return projects
}

internal fun getPublicationInformation(gradleProject: GradleProject): Triple<String, String, String> {
  val publication = gradleProject.publication
    ?: throw IllegalStateException("Publication information not found for project ${gradleProject.name}")
  return Triple(publication.groupId, publication.artifactId, publication.version)
}

internal fun getPublishingExtension(project: Project): PublishingExtension {
  return project.extensions.findByName("publishing")
    as? org.gradle.api.publish.PublishingExtension
    ?: throw IllegalStateException(
      "`publishing` extension not found. Please apply `maven-publish` plugin in root project."
    )
}

internal fun getConfigExtension(project: Project): ExpoPublishExtension {
  return project.rootProject.extensions
    .findByType(ExpoPublishExtension::class.java)
    ?: throw IllegalStateException("`ExpoPublishExtension` not found or not configured. Please, make sure that `expoBrownfieldPublishPlugin` was called in `build.gradle`.")
}

/**
 * Get the React Native version for the project.
 * 
 * @param project The project to get the React Native version for.
 * @return The React Native version for the project.
 * @throws IllegalStateException if the React Native version cannot be inferred.
 */
internal fun getReactNativeVersion(project: Project): String {
  return try {
    val process = ProcessBuilder(
      "node",
      "--print",
      "require('react-native/package.json').version"
    )
      .directory(project.rootProject.projectDir)
      .redirectErrorStream(true)
      .start()

    val version = process.inputStream.bufferedReader().readText().trim()
    process.waitFor()
    
    if (process.exitValue() == 0 && version.isNotEmpty()) {
      return version
    }

    throw IllegalStateException("Failed to infer React Native version via Node")      
  } catch (e: Exception) {
    project.logger.warn("Failed to infer React Native version via Node")
    project.logger.warn("Falling back to reading from package.json...")
    return getReactNativeVersionFromPackageJson(project)
  }
}

/**
 * Get the React Native version from the package.json file.
 * 
 * This method is used as a fallback when the React Native version cannot be inferred via Node.
 * 
 * @param project The project to get the React Native version from the package.json file for.
 * @return The React Native version from the package.json file.
 * @throws IllegalStateException if the React Native version cannot be inferred from the package.json file.
 */
internal fun getReactNativeVersionFromPackageJson(project: Project): String {
  val packageJson = project.rootProject.projectDir.parentFile.resolve("package.json")
  if (!packageJson.exists()) {
    throw IllegalStateException("package.json not found in ${project.rootProject.projectDir}")
  }

  val slurper = JsonSlurper()
  val json = slurper.parse(packageJson) as Map<*, *>
  
  val dependencies = json["dependencies"] as? Map<*, *>
  val devDependencies = json["devDependencies"] as? Map<*, *>
  
  val version = (dependencies?.get("react-native") as? String)
    ?: (devDependencies?.get("react-native") as? String)
    ?: throw IllegalStateException("react-native not found in package.json dependencies")
  
  return version.removePrefix("^").removePrefix("~")
}

/**
 * Parse the module.json file.
 * 
 * @param moduleFile The module.json file to parse.
 * @return The parsed module.json file.
 * @throws IllegalStateException if the module.json file cannot be parsed.
 */
internal fun parseModuleJson(moduleFile: File): Map<String, Any>? {
  val slurper = JsonSlurper()

  @Suppress("UNCHECKED_CAST")
  return slurper.parseText(moduleFile.readText()) as? Map<String, Any>
     ?: throw IllegalStateException("Failed to parse module file: ${moduleFile.path}")
}

/**
 * Remove react-native dependency from the POM file.
 * 
 * com.facebook.react:react-native is deprecated and has to be stripped
 * similarly to what React Native Gradle plugin does.
 * 
 * @param xml The XML provider to modify.
 */
internal fun removeReactNativeDependencyPom(xml: XmlProvider) {
  val dependencyNodes = xml.dependencyNodes()
  val toRemove = mutableListOf<Node>()
  
  dependencyNodes.forEach { dependency ->
    if (
      dependency.groupId() == "com.facebook.react" && 
      dependency.artifactId() == "react-native"
    ) {
      toRemove.add(dependency)
    }
  }
  
  val dependenciesNode = xml.dependenciesNode()
  toRemove.forEach { dependency ->
    dependenciesNode?.remove(dependency)
  }
}

/**
 * Set the React Native version in the POM file.
 * 
 * @param xml The XML provider to modify.
 * @param rnVersion The React Native version to set.
 */
internal fun setReactNativeVersionPom(xml: XmlProvider, rnVersion: String) {
  xml.dependencyNodes().forEach { dependency ->
    if (
      dependency.groupId() == "com.facebook.react" && 
      (dependency.artifactId() == "react-android" || dependency.artifactId() == "hermes-android")
    ) {
      dependency.setVersion(rnVersion)
    }
  }
}
