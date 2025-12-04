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
  project.extensions.getByType(AndroidComponentsExtension::class.java).finalizeDsl {
    val libraryExtension = project.extensions.getByType(LibraryExtension::class.java)
    libraryExtension.applyPublishingVariant()

    project.afterEvaluate {
      val publicationExtension = project.extensions.getByType(PublishingExtension::class.java)

      if (
        project.components.getByName("prebuildRelease") == null ||
        publicationExtension == null
      ) {
        println("Skipping ${project.name} as it can't be published due to missing components or publishing extension")
        return@afterEvaluate
      }

      publicationExtension.publications.create(
        "default",
        MavenPublication::class.java
      ) { mavenPublication ->
        with(mavenPublication) {
          from(project.components.getByName("prebuildRelease"))
          groupId = project.group.toString()
          artifactId = requireNotNull(libraryExtension.namespace)
          version = requireNotNull(libraryExtension.defaultConfig.versionName ?: "1.0.0")

          pom.withXml { xml ->
            removeReactNativeDependencyPom(xml)
          }
        }
      }

      removeReactNativeDependencyModule(project)

      publicationExtension.repositories.maven { repo ->
        repo.name = "customLocal"
        repo.url = project.uri("file://${project.rootProject.projectDir.parentFile}/maven")
      }
    }
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
  val removeDependenciesTask = project.tasks.register("removeRNDependencyFromModuleFile") { task ->
    task.doLast {
      val moduleBuildDir = project.layout.buildDirectory.get().asFile
      val moduleFile = File(moduleBuildDir, "publications/default/module.json")
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

  val task = project.tasks.named("generateMetadataFileForDefaultPublication")
  if (task != null) {
    task.configure {
      it.finalizedBy(removeDependenciesTask)
    }
  }
}
