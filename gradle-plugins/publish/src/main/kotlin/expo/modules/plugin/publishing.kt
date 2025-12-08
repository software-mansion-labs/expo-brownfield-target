package expo.modules.plugin

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.XmlProvider
import org.gradle.api.tasks.Copy
import java.io.File
import groovy.util.Node
import groovy.util.NodeList
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

internal fun setupPublishing(project: Project) {
  val variants = listOf("brownfieldDebug", "brownfieldRelease", "brownfieldAll")
  
  project.extensions.getByType(AndroidComponentsExtension::class.java).finalizeDsl {
    val libraryExtension = project.extensions.getByType(LibraryExtension::class.java)
    libraryExtension.applyPublishingVariant()

    project.afterEvaluate {
      val configExtension = getConfigExtension(project)
      val publicationExtension = project.extensions.getByType(PublishingExtension::class.java)

      if (
        publicationExtension == null ||
        variants.any { project.components.getByName(it) == null }
      ) {
        println("Skipping ${project.name} as it can't be published due to missing publishing variants (\"brownfieldDebug, etc.\") or publishing extension")
        return@afterEvaluate
      }

      val isBrownfieldProject = project.name == configExtension.libraryName.get()
      variants.forEach { variant ->
        publicationExtension.createPublication(variant, project, libraryExtension, isBrownfieldProject)
      }
      
      if (!isBrownfieldProject) {
        removeReactNativeDependencyModule(project)
      }
      setupRepositories(publicationExtension, project, configExtension)
    }
  }
}

internal fun setupRepositories(publicationExtension: PublishingExtension, project: Project, configExtension: ExpoPublishExtension) {
  if (configExtension.publications.isEmpty) {
    throw IllegalStateException(
      "`publications` is not set. Please, make sure that `publications { ... }` was called in the root `build.gradle` file."
    )
  }

  configExtension.publications.forEach { publication ->
    publicationExtension.setupRepository(publication, project)
  }
}

internal fun removeReactNativeDependencyPom(xml: XmlProvider) {
  val dependencyNodes = xml.dependencyNodes()
  val toRemove = mutableListOf<Node>()
  
  dependencyNodes.forEach { dependency ->
    if (
      dependency.groupId() == "com.facebook.react" && 
      dependency.artifactId() == "react-native"
    ) {
      toRemove.add(dependency)
    }
  }
  
  val dependenciesNode = xml.dependenciesNode()
  toRemove.forEach { dependency ->
    dependenciesNode?.remove(dependency)
  }
}

internal fun removeReactNativeDependencyModule(project: Project) {
  val vairants = listOf("brownfieldDebug", "brownfieldRelease", "brownfieldAll")
  vairants.forEach { variant ->
    createRemoveReactNativeDependencyModuleTask(project, variant)
  }
}

internal fun createRemoveReactNativeDependencyModuleTask(project: Project, variant: String) {
  val removeDependenciesTask = project.tasks.register("removeRNDependencyFromModuleFile$variant") { task ->
    task.doLast {
      val moduleBuildDir = project.layout.buildDirectory.get().asFile
      val moduleFile = File(moduleBuildDir, "publications/$variant/module.json")
      if (!moduleFile.exists()) {
        println("WARNING: Module file for project: ${project.name} does not exist at: ${moduleFile.path}")
        println("This file might not need to be modified. Continuing tasks...")
        return@doLast
      }

      @Suppress("UNCHECKED_CAST")
      val moduleJson = JsonSlurper().parseText(moduleFile.readText()) as? Map<String, Any>
      if (moduleJson == null) {
        println("WARNING: Failed to parse module file for project: ${project.name}")
        println("This file might not need to be modified. Continuing tasks...")
        return@doLast
      }

      @Suppress("UNCHECKED_CAST")
      (moduleJson["variants"] as? List<MutableMap<String, Any>>)?.forEach { variant ->
        @Suppress("UNCHECKED_CAST")
        (variant["dependencies"] as? MutableList<MutableMap<String, Any>>)?.removeAll {
          it["group"] == "com.facebook.react" && it["module"] == "react-native"
        }
      }

      moduleFile.writeText(JsonOutput.prettyPrint(JsonOutput.toJson(moduleJson)))
    }
  }

  val taskName = "generateMetadataFileFor${variant.capitalized()}Publication"
  project.tasks.named(taskName).configure {
    it.finalizedBy(removeDependenciesTask)
  }
}
