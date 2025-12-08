package expo.modules.plugin

import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository
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
  when (publication.type.get()) {
    "localDirectory", "localMaven" -> {
      createPrebuiltsCopyTask(publication, rootProject)
    }
    else -> {
      createPrebuiltsPublishTask(publication, rootProject)
    }
  }
}

internal fun createPrebuiltsCopyTask(
  publication: PublicationConfig,
  rootProject: Project
) {
  val brownfieldProject = getBrownfieldProject(rootProject)
  val projects = getExpoPrebuiltProjects(rootProject)

  brownfieldProject.afterEvaluate {
    val copyTask = brownfieldProject.tasks.register("copyPrebuiltExpoModules${publication.getName()}", Copy::class.java) { task ->
      projects.forEach { project ->
          task.from(project.localMavenRepo()) { copy ->
              copy.include("**/*")
          }
      }
  
      if (publication.type.get() == "localDirectory") {
        task.into(rootProject.file("${publication.url.get()}"))
      } else {
        val m2Repo = File(System.getProperty("user.home"))
          .resolve(".m2/repository")
        task.into(m2Repo)
      }
    }
  
    val tasks = listOf(
      "build"
      // "generateMetadataFileForBrownfieldDebugPublication",
      // "generateMetadataFileForBrownfieldReleasePublication",
      // "generateMetadataFileForBrownfieldAllPublication"
    )
    tasks.forEach { task ->
      brownfieldProject.tasks.named(task).configure {
        it.finalizedBy(copyTask)
      }
    }
  }
}


internal fun createPrebuiltsPublishTask(
  publication: PublicationConfig,
  rootProject: Project
) {
  val brownfieldProject = getBrownfieldProject(rootProject)
  val publishingExtension = getPublishingExtension(brownfieldProject)
  val projects = getExpoPrebuiltProjects(rootProject)

  brownfieldProject.afterEvaluate {
    projects.forEach { project ->
      val (_groupId, _artifactId, _version) = getPublicationInformation(project)

      publishingExtension.publications.create(
        "publishPrebuilt${project.getCapitalizedName()}${publication.getName()}",
        MavenPublication::class.java
      ) { mavenPublication ->
        with(mavenPublication) {
          groupId = _groupId
          artifactId = _artifactId
          version = _version

          pom.withXml { xmlProvider ->
            val pomFile = project.localMavenRepo()
              .resolve("${_groupId.replace('.', '/')}/${_artifactId}/${_version}/${_artifactId}-${_version}.pom")
            if (!pomFile.exists()) {
              throw IllegalStateException("Expo module pom not found: $pomFile")
            }

            val xmlContent = xmlProvider.asString()
            xmlContent.setLength(0)
            xmlContent.append(pomFile.readText())
          }

          project.localMavenRepo()
            .resolve("${_groupId.replace('.', '/')}/${_artifactId}/${_version}")
            .listFiles()
            ?.filter { file ->
              when (file.extension) {
                "aar", "jar", "module" -> true
                else -> false
              }
            }
            ?.forEach { file ->
              artifact(file)
            }
        }
      }
    }

    publishingExtension.setupRepository(publication, brownfieldProject)

    val publishTasks = publishingExtension.publications.toList()
      .filter { it.name.startsWith("publishPrebuiltExpo") }
      .map { pub ->
        brownfieldProject.tasks.named(
          "publish${pub.name.capitalized()}PublicationTo${publication.getName().capitalized()}Repository"
        )
      }

    // TODO: Deduplicate
    val tasks = listOf(
      "build"
      // "generateMetadataFileForBrownfieldDebugPublication",
      // "generateMetadataFileForBrownfieldReleasePublication",
      // "generateMetadataFileForBrownfieldAllPublication"
    )

    tasks.forEach { task ->
      brownfieldProject.tasks.named(task).configure {
        it.finalizedBy(publishTasks)
      }
    }
  }
}

// internal fun wirePrebuiltsPublicationTask() {
//   val tasks = listOf(
//     "generateMetadataFileForBrownfieldDebugPublication",
//     "generateMetadataFileForBrownfieldReleasePublication",
//     "generateMetadataFileForBrownfieldAllPublication"
//   )
// }
