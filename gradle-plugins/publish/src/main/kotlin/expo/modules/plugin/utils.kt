package expo.modules.plugin

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import expo.modules.plugin.ExpoGradleExtension
import expo.modules.plugin.configuration.GradleProject

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
