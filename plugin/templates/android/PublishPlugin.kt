package com.pmleczek.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import com.android.build.gradle.LibraryExtension

class ExpoBrownfieldPublishPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    println("ExpoBrownfieldPublishPlugin applied to project: ${project.name}")
    configurePlugins(project)
    configurePublishing(project)
  }

  private fun configurePlugins(project: Project) {
    project.plugins.apply("maven-publish")
  }

  private fun configurePublishing(project: Project) {
    var publishing = project.extensions.getByType(PublishingExtension::class.java)
    val existingPublication = publishing.publications.findByName("mavenAar") as? MavenPublication

    if (existingPublication != null) {
      if (publishing.repositories.isEmpty()) {
        publishing.repositories {
          mavenLocal()
        }
      }
    } else {
      val group = project.group?.toString()
      val artifact = project.name
      val projectVersion = project.version?.toString() ?: "0.0.1"
      var useFallback = false

      publishing.publications.create("mavenAar", MavenPublication::class.java) {
        groupId = group
        artifactId = artifact
        version = projectVersion
        
        if (project.components.findByName("release") != null) {
          from(project.components.getByName("release"))
        } else {
          useFallback = true
        }
        
        // pom {
        //   withXml {
        //     // Konfiguracja POM
        //   }
        // }
      }
      
      publishing.repositories {
        mavenLocal()
      }
      
      // TODO: Fix to use dynamic name - maybe from variable interpolation
      val brownfieldProject = project.rootProject.project(":brownfield")
      if (brownfieldProject == null) {
        throw IllegalStateException("Brownfield project not found")
      }

      val brownfieldAssembleTask = brownfieldProject.tasks.findByName("assembleRelease")
      if (brownfieldAssembleTask == null) {
        throw IllegalStateException("Brownfield assembleRelease task not found")
      }

      var publishTask = project.tasks.findByName("publishMavenAarPublicationToMavenLocal")
      if (publishTask == null && !useFallback) {
        throw IllegalStateException("Publish task not found")
      } else if (useFallback) {
        publishTask = project.tasks.findByName("publishToMavenLocal")
        if (publishTask == null) {
          throw IllegalStateException("Publish task not found")
        }
      }

      brownfieldAssembleTask.finalizedBy(publishTask)
    }
  }
}
