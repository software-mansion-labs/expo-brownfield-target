package expo.modules.plugin

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class ExpoBrownfieldPublishPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.plugins.apply("maven-publish")

    project.afterEvaluate { project ->
      if (shouldSkip(project)) {
        println("Skipping ${project.name} as it is not a library project which should be published")
        return@afterEvaluate
      }

      setupPublishing(project)     
    }
  }

  private fun shouldSkip(project: Project): Boolean {
    val appProject = findAppProject(project)
    return project.extensions.findByType(AndroidComponentsExtension::class.java) == null ||
      project.extensions.findByType(LibraryExtension::class.java) == null ||
      listOf("${appProject.name}", "brownfield").contains(project.name)
  }
}
