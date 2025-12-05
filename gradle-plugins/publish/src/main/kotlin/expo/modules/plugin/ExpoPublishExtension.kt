package expo.modules.plugin

import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

abstract class PublicationConfig @Inject constructor(
    name: String,
    objects: ObjectFactory
) : Named {
    private val _name: String = name
    override fun getName(): String = _name
    
    abstract val type: Property<String>
    abstract val url: Property<String>
    abstract val username: Property<String>
    abstract val password: Property<String>
    abstract val token: Property<String>
    
    init {
        url.convention("")
        username.convention("")
        password.convention("")
        token.convention("")
    }
}

/**
 * The ExpoPublishExtension class defines a custom extension for the plugin.
 * This allows users to configure the plugin in their build script via a DSL block, e.g.:
 *
 * expoBrownfieldPublishPlugin {
 *     publications {
 *         localDefault {
 *             type = PublicationType.localMaven
 *         }
 *         private {
 *             type = PublicationType.remotePrivateBasic
 *             url = "https://maven.example.com"
 *             username = "username"
 *             password = "password"
 *         }
 *     }
 * }
 */
abstract class ExpoPublishExtension @Inject constructor(objects: ObjectFactory) {
  abstract var publications: NamedDomainObjectContainer<PublicationConfig>
    
  init {
      publications = objects.domainObjectContainer(PublicationConfig::class.java) { name ->
          objects.newInstance(PublicationConfig::class.java, name, objects)
      }
  }
}