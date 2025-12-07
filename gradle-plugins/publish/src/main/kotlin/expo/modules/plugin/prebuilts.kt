package expo.modules.plugin

import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import expo.modules.plugin.ExpoGradleExtension
import expo.modules.plugin.configuration.GradleProject
import java.io.File

internal fun setupPrebuiltsCopying(rootProject: Project) {
  rootProject.afterEvaluate {
    val publications = rootProject.extensions
    .findByType(ExpoPublishExtension::class.java)?.publications
    ?: throw IllegalStateException("`ExpoPublishExtension` not found or not configured. Please, make sure that `expoBrownfieldPublishPlugin` was called in `build.gradle`.")

    if (publications.isEmpty) {
      throw IllegalStateException(
        "`publications` is not set. Please, make sure that `publications { ... }` was called in the root `build.gradle` file."
      )
    }

    publications.forEach { publication ->
      createPrebuiltsPublicationTask(publication, rootProject)
    }
  }
}

internal fun createPrebuiltsPublicationTask(
  publication: PublicationConfig,
  rootProject: Project
) {
  if (publication.type.get() == "localDirectory") {
    createPrebuiltsCopyTask(publication, rootProject)
    return
  }

  // TODO: Implement for other publication types
}

internal fun createPrebuiltsCopyTask(
  publication: PublicationConfig,
  rootProject: Project
) {
  val brownfieldProject = rootProject.project(":brownfield")
  if (brownfieldProject == null) {
    throw IllegalStateException("Brownfield project not found in the root project")
  }

  val gradleExtension = rootProject.gradle.extensions.findByType(ExpoGradleExtension::class.java)
      ?: throw IllegalStateException("`ExpoGradleExtension` not found. Please, make sure that `useExpoModules` was called in `settings.gradle`.")
  val config = gradleExtension.config
  val projects = config.allProjects.filter { it.usePublication }

  brownfieldProject.afterEvaluate {
    val copyTask = brownfieldProject.tasks.register("publishSelectedExpoModules", Copy::class.java) { task ->
      projects.forEach { project ->
          val sourceDirFile = File(project.sourceDir)
          val fromPath = sourceDirFile.parentFile.resolve("local-maven-repo")
          task.from(fromPath) { copy ->
              copy.include("**/*")
          }
      }
  
      task.into(rootProject.file("${publication.url.get()}"))
    }
  
    val tasks = listOf(
      "generateMetadataFileForBrownfieldDebugPublication",
      "generateMetadataFileForBrownfieldReleasePublication",
      "generateMetadataFileForBrownfieldAllPublication"
    )
    tasks.forEach { task ->
      brownfieldProject.tasks.named(task).configure {
        it.finalizedBy(copyTask)
      }
    }
  }
}
