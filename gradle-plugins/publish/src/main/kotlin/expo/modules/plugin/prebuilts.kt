package expo.modules.plugin

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository
import expo.modules.plugin.ExpoGradleExtension
import expo.modules.plugin.configuration.GradleProject
import java.io.File

internal fun setupPrebuiltsCopying(rootProject: Project) {
  rootProject.afterEvaluate {
    val configExtension = getConfigExtension(rootProject)

    if (configExtension.publications.isEmpty) {
      throw IllegalStateException(
        "`publications` is not set. Please, make sure that `publications { ... }` was called in the root `build.gradle` file."
      )
    }

    configExtension.publications.forEach { publication ->
      createPrebuiltsPublicationTask(
        publication, 
        rootProject, 
        configExtension.libraryName.get()
      )
    }
  }
}

internal fun createPrebuiltsPublicationTask(
  publication: PublicationConfig,
  rootProject: Project,
  libraryName: String
) {
  when (publication.type.get()) {
    "localDirectory", "localMaven" -> {
      createPrebuiltsCopyTask(publication, rootProject, libraryName)
    }
    else -> {
      createPrebuiltsPublishTask(publication, rootProject, libraryName)
    }
  }
}

internal fun createPrebuiltsCopyTask(
  publication: PublicationConfig,
  rootProject: Project,
  libraryName: String
) {
  val brownfieldProject = getBrownfieldProject(rootProject, libraryName)
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
  
    registerPrebuiltPublicationTask(brownfieldProject, task = copyTask)
  }
}


internal fun createPrebuiltsPublishTask(
  publication: PublicationConfig,
  rootProject: Project,
  libraryName: String
) {
  val brownfieldProject = getBrownfieldProject(rootProject, libraryName)
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

    registerPrebuiltPublicationTask(brownfieldProject, tasks = publishTasks)
  }
}

internal fun registerPrebuiltPublicationTask(brownfieldProject: Project, task: TaskProvider<Copy>? = null, tasks: List<TaskProvider<Task>> = listOf()) {
  brownfieldProject.tasks.named("build").configure {
    it.finalizedBy(task ?: tasks)
  }
}
