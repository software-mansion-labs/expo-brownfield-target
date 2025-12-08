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
// TODO: Update name?
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

internal fun XmlProvider.dependencyNodes(): List<Node> {
  val dependenciesNode = dependenciesNode()
  if (dependenciesNode == null) {
    return emptyList()
  }

  return dependenciesNode.children().filterIsInstance<Node>()
}
// END SECTION: XmlProvider

// SECTION: Node
internal fun Node.groupId(): String? {
  val groupIdNode = when (val g = this.get("groupId")) {
    is Node -> g
    is NodeList -> if (g.size > 0) g[0] as? Node else null
    else -> null
  }

  return groupIdNode?.text()
}

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
internal fun String.capitalized(): String {
  return this.replaceFirstChar { it.uppercase() }
}
// END SECTION: String

// SECTION: PublicationExtension
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
          // TODO: Handle properly
          maven.isAllowInsecureProtocol = publication.type.get() == "remotePublic"
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
          // TODO: Handle properly
          maven.isAllowInsecureProtocol = true
        }
      }
    }
  }
}

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
internal fun GradleProject.localMavenRepo(): File {
  return File(sourceDir).parentFile.resolve("local-maven-repo")
}

internal fun GradleProject.getCapitalizedName(): String {
  return name
    .split('-')
    .map { it.capitalized() }
    .joinToString("")
}
// END SECTION: GradleProject
