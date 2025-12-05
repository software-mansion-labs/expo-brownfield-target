package expo.modules.plugin

import com.android.build.gradle.LibraryExtension
import groovy.util.Node
import groovy.util.NodeList
import org.gradle.api.XmlProvider

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
