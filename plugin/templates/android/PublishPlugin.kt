package com.pmleczek.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import com.android.build.gradle.LibraryExtension
import java.io.File

class ExpoBrownfieldPublishPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    println("ExpoBrownfieldPublishPlugin applied to project: ${project.name}")
    configurePlugins(project)

    if (project.state.executed) {
      configurePublishing(project)
    } else {
      project.afterEvaluate {
        configurePublishing(project)
      }
    }
  }

  private fun configurePlugins(project: Project) {
    if (project.plugins.findPlugin("maven-publish") == null) {
      project.plugins.apply("maven-publish")
    }
  }

  private fun configureExistingPublication(project: Project): Boolean {
    val publishing = project.extensions.getByType(PublishingExtension::class.java)
    val existingPublication = publishing.publications.findByName("mavenAar") as? MavenPublication

    if (existingPublication != null) {
      if (publishing.repositories.isEmpty()) {
        publishing.repositories {
          mavenLocal()
        }
      }
      return true
    }

    return false
  }

  private fun configurePublicationFromRelease(project: Project) {
    var publishing = project.extensions.getByType(PublishingExtension::class.java)

    val group = project.group?.toString()
    val artifact = project.name
    val projectVersion = project.version?.toString() ?: "0.0.1"
  
    publishing.publications.create("mavenAar", MavenPublication::class.java) {
      groupId = project.group?.toString()
      artifactId = project.name
      version = project.version?.toString() ?: "0.0.1"
      
      from(project.components.getByName("release"))
    }

    publishing.repositories {
      mavenLocal()
    }

    val brownfieldProject = project.rootProject.project(":brownfield")
    if (brownfieldProject == null) {
      throw IllegalStateException("Brownfield project not found")
    }

    val brownfieldAssembleTask = brownfieldProject.tasks.findByName("assembleRelease")
    if (brownfieldAssembleTask == null) {
      throw IllegalStateException("Brownfield assembleRelease task not found")
    }

    brownfieldAssembleTask.finalizedBy(project.tasks.findByName("publishMavenAarPublicationToMavenLocal"))
  }

  private fun configurePublicationFromOutputs(project: Project) {
    val brownfieldProject = project.rootProject.findProject(":brownfield")
    if (brownfieldProject == null) {
      throw IllegalStateException("Brownfield project not found")
    }

    var publishing = project.extensions.getByType(PublishingExtension::class.java)
    val group = project.group?.toString()
    val artifact = project.name
    val projectVersion = project.version?.toString() ?: "0.0.1"

    publishing.publications.create("mavenAar", MavenPublication::class.java) {
      groupId = group
      artifactId = artifact
      version = projectVersion
      
      project.gradle.projectsEvaluated {
        val bundleAarTask = project.tasks.findByName("bundleReleaseAar")
        if (bundleAarTask != null) {
          val aarFile = File(bundleAarTask.outputs.files.singleFile.absolutePath)
          artifact(aarFile) {
            extension = "aar"
            builtBy(bundleAarTask)
          }
        }
      }
    }

    publishing.repositories {
      mavenLocal()
    }

    project.gradle.projectsEvaluated {
      val brownfieldAssembleTask = brownfieldProject.tasks.findByName("assembleRelease")
      val publishTask = project.tasks.findByName("publishMavenAarPublicationToMavenLocal")
      
      if (brownfieldAssembleTask != null && publishTask != null) {
        brownfieldAssembleTask.finalizedBy(publishTask)
      }
    }
  }

  private fun configurePublishing(project: Project) {
    var publishing = project.extensions.getByType(PublishingExtension::class.java)
    val existingPublication = publishing.publications.findByName("mavenAar") as? MavenPublication

    if (!configureExistingPublication(project)) {
      if (project.components.findByName("release") != null) {
        configurePublicationFromRelease(project)
      } else {
        configurePublicationFromOutputs(project)
      }
    }
  }
}
