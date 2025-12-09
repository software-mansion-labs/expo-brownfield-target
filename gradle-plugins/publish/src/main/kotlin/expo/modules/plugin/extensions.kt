package expo.modules.plugin

import com.android.build.gradle.LibraryExtension
import groovy.util.Node
import groovy.util.NodeList
import org.gradle.api.Project
import org.gradle.api.XmlProvider
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.artifacts.repositories.AuthenticationSupported
import org.gradle.authentication.http.HttpHeaderAuthentication
import org.gradle.api.credentials.HttpHeaderCredentials
import expo.modules.plugin.configuration.GradleProject
import java.io.File

// SECTION: LibraryExtension
/**
 * Apply publishing variants to the library extension.
 * 
 * @return The library extension.
 */
internal fun LibraryExtension.applyPublishingVariant() {
  publishing { publishing ->
      publishing.multipleVariants("brownfieldDebug") {
        includeBuildTypeValues("debug")
        withSourcesJar()
      }

      publishing.multipleVariants("brownfieldRelease") {
        includeBuildTypeValues("release")
        withSourcesJar()
      }

      publishing.multipleVariants("brownfieldAll") {
        includeBuildTypeValues("debug", "release")
        withSourcesJar()
      }
  }
}
// END SECTION: LibraryExtension

// SECTION: XmlProvider
/**
 * Get the dependencies node from the XML provider.
 * 
 * @return The dependencies node, or null if not found.
 */
internal fun XmlProvider.dependenciesNode(): Node? {
  val root = asNode() as? Node
  if (root == null) {
    return null
  }

  val dependenciesNodeList = root.get("dependencies") as? NodeList              
  val dependenciesNode = if (dependenciesNodeList != null && dependenciesNodeList.size > 0) {
    dependenciesNodeList[0] as? Node
  } else {
    null
  }

  return dependenciesNode
}

/**
 * Get the list of dependency nodes from the XML provider.
 * 
 * @return The list of dependency nodes, or empty list if not found.
 */
internal fun XmlProvider.dependencyNodes(): List<Node> {
  val dependenciesNode = dependenciesNode()
  if (dependenciesNode == null) {
    return emptyList()
  }

  return dependenciesNode.children().filterIsInstance<Node>()
}
// END SECTION: XmlProvider

// SECTION: Node
/**
 * Get the groupId of the dependency.
 * 
 * @return The groupId of the dependency, or null if not found.
 */
internal fun Node.groupId(): String? {
  val groupIdNode = when (val g = this.get("groupId")) {
    is Node -> g
    is NodeList -> if (g.size > 0) g[0] as? Node else null
    else -> null
  }

  return groupIdNode?.text()
}

/**
 * Get the artifactId of the dependency.
 * 
 * @return The artifactId of the dependency, or null if not found.
 */
internal fun Node.artifactId(): String? {
  val artifactIdNode = when (val a = this.get("artifactId")) {
    is Node -> a
    is NodeList -> if (a.size > 0) a[0] as? Node else null
    else -> null
  }

  return artifactIdNode?.text()
}
// END SECTION: Node

// SECTION: String
/**
 * Capitalize the first letter of the string.
 * 
 * @return The capitalized string.
 */
internal fun String.capitalized(): String {
  return this.replaceFirstChar { it.uppercase() }
}
// END SECTION: String

// SECTION: PublicationExtension
/**
 * Set up a repository for the publication.
 * 
 * @param publication The publication configuration to use.
 * @param project The project to set up the repository for.
 */
internal fun PublishingExtension.setupRepository(publication: PublicationConfig, project: Project) {
  when(publication.type.get()) {
    "localMaven" -> {
      repositories { repo ->
        repo.mavenLocal()
      }
    }
    "localDirectory", "remotePublic" -> {
      repositories { repo ->
        repo.maven { maven ->
          maven.name = publication.getName()
          maven.url = project.uri("${publication.url.get()}")
          maven.isAllowInsecureProtocol = publication.allowInsecure.get()
        }
      }
    }
    "remotePrivate" -> {
      repositories { repo ->
        repo.maven { maven ->
          maven.name = publication.getName()
          maven.url = project.uri("${publication.url.get()}")
          maven.credentials { credentials ->
            credentials.username = publication.username.get()
            credentials.password = publication.password.get()
          }
          maven.isAllowInsecureProtocol = publication.allowInsecure.get()
        }
      }
    }
  }
}

/**
 * Create a publication for the project.
 * 
 * @param from The variant to create the publication for.
 * @param project The project to create the publication for.
 * @param libraryExtension The library extension to use.
 * @param isBrownfieldProject Whether the project is a brownfield project.
 */
internal fun PublishingExtension.createPublication(
  from: String,
  project: Project,
  libraryExtension: LibraryExtension,
  isBrownfieldProject: Boolean = false
) {
  val _artifactId = if (isBrownfieldProject) {
    project.name
  } else {
    requireNotNull(libraryExtension.namespace)
  }
  
  publications.create(
    from,
    MavenPublication::class.java
  ) { mavenPublication ->
    with(mavenPublication) {
      from(project.components.getByName(from))
      groupId = project.group.toString()
      artifactId = _artifactId
      version = requireNotNull(libraryExtension.defaultConfig.versionName ?: "1.0.0")

      pom.withXml { xml ->
        removeReactNativeDependencyPom(xml)
      }
    }
  }
}
// END SECTION: PublicationExtension

// SECTION: GradleProject
/**
 * Get the local Maven repository for the project.
 * 
 * @return The local Maven repository for the project.
 */
internal fun GradleProject.localMavenRepo(): File {
  return File(sourceDir).parentFile.resolve("local-maven-repo")
}

/**
 * Get the capitalized name of the project.
 * 
 * @return The capitalized name of the project.
 */
internal fun GradleProject.getCapitalizedName(): String {
  return name
    .split('-')
    .map { it.capitalized() }
    .joinToString("")
}
// END SECTION: GradleProject
