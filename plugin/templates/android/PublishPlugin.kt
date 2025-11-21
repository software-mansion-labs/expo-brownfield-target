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

    // if (existingPublication != null) {
    //   if (publishing.repositories.isEmpty()) {
    //     publishing.repositories {
    //       mavenLocal()
    //     }
    //   }
    // } else {
    //   val _group = project.group?.toString()
    //   val artifact = project.name
    //   val projectVersion = project.version?.toString() ?: "0.0.1"
    //   var useFallback = false

    //   publishing.publications.create("mavenAar", MavenPublication::class.java) {
    //     groupId = _group
    //     artifactId = artifact
    //     version = projectVersion
        
    //     if (project.components.findByName("release") != null) {
    //       from(project.components.getByName("release"))
    //     } else {
    //       project.tasks.register("findAarTask") {
    //         group = "help"
    //         description = "Scans all tasks to find who produces an .aar file"
            
    //         doLast {
    //             val red = "\u001B[31m"
    //             val green = "\u001B[32m"
    //             val reset = "\u001B[0m"
                
    //             println("🔍 Scanning project '${project.name}' for AAR producers...")
      
    //             // Iterate over ALL tasks in the project
    //             project.tasks.forEach { task ->
    //                 try {
    //                     // Check output files
    //                     task.outputs.files.forEach { file ->
    //                         if (file.name.endsWith(".aar")) {
    //                             println("${green}FOUND TASK: ${task.name}${reset}")
    //                             println("   Path: ${file.absolutePath}")
    //                         }
    //                     }
    //                 } catch (e: Exception) {
    //                     // Some tasks (like clean) might throw strict exceptions if inspected weirdly
    //                 }
    //             }
    //         }
    //     }
      
    //     val brownfieldProject = project.rootProject.project(":brownfield")
    //         if (brownfieldProject == null) {
    //           throw IllegalStateException("Brownfield project not found")
    //         }
      
    //         val brownfieldAssembleTask = brownfieldProject.tasks.findByName("assembleRelease")
    //         if (brownfieldAssembleTask == null) {
    //           throw IllegalStateException("Brownfield assembleRelease task not found")
    //         }
      
      
    //         brownfieldAssembleTask.finalizedBy(project.tasks.findByName("findAarTask"))
    //     }
        
    //     // pom {
    //     //   withXml {
    //     //     // Konfiguracja POM
    //     //   }
    //     // }
    //   }
      
    //   publishing.repositories {
    //     mavenLocal()
    //   }
      
    //   // TODO: Fix to use dynamic name - maybe from variable interpolation
    //   val brownfieldProject = project.rootProject.project(":brownfield")
    //   if (brownfieldProject == null) {
    //     throw IllegalStateException("Brownfield project not found")
    //   }

    //   val brownfieldAssembleTask = brownfieldProject.tasks.findByName("assembleRelease")
    //   if (brownfieldAssembleTask == null) {
    //     throw IllegalStateException("Brownfield assembleRelease task not found")
    //   }

    //   var publishTask = project.tasks.findByName("publishMavenAarPublicationToMavenLocal")
    //   if (publishTask == null && !useFallback) {
    //     throw IllegalStateException("Publish task not found")
    //   } else if (useFallback) {
    //     publishTask = project.tasks.findByName("publishToMavenLocal")
    //     if (publishTask == null) {
    //       throw IllegalStateException("Publish task not found")
    //     }
    //   }

    //   brownfieldAssembleTask.finalizedBy(publishTask)
    // }
  }
}
