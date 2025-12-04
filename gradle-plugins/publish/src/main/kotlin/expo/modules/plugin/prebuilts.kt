package expo.modules.plugin

import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import expo.modules.plugin.ExpoGradleExtension
import expo.modules.plugin.configuration.GradleProject
import java.io.File

internal fun setupPrebuiltsCopying(rootProject: Project) {
  val gradleExtension = rootProject.gradle.extensions.findByType(ExpoGradleExtension::class.java)
      ?: throw IllegalStateException("`ExpoGradleExtension` not found. Please, make sure that `useExpoModules` was called in `settings.gradle`.")
  val config = gradleExtension.config
  val projects = config.allProjects.filter { it.usePublication }

  val brownfieldProject = rootProject.project(":brownfield")
  if (brownfieldProject == null) {
    throw IllegalStateException("Brownfield project not found in the root project")
  }

  brownfieldProject.afterEvaluate {
    val copyTask = brownfieldProject.tasks.register("publishSelectedExpoModules", Copy::class.java) { task ->
      projects.forEach { project ->
          val sourceDirFile = File(project.sourceDir)
          val fromPath = sourceDirFile.parentFile.resolve("local-maven-repo")
          task.from(fromPath) { copy ->
              copy.include("**/*")
          }
      }
  
      task.into(rootProject.file("../maven"))
    }
  
    brownfieldProject.tasks.named("generateMetadataFileForMavenAarPublication").configure {
      it.finalizedBy(copyTask)
    }
  }
}
