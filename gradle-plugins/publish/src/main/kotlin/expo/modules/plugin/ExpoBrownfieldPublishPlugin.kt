package expo.modules.plugin

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class ExpoBrownfieldPublishPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.plugins.apply("maven-publish")

    project.afterEvaluate { project ->
      if (project.shouldBeSkipped()) {
        println("Skipping ${project.name} as it is not a project which should be published")
        return@afterEvaluate
      }

      setupPublishing(project)     
    }
  }
}
