package buildLogic.convention.extensions.plugins

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

open class JvmServerExtension @Inject constructor(objects: ObjectFactory) {
    val jvmTarget: Property<Int> = objects.property(Int::class.java)
    val mainClass: Property<String> = objects.property(String::class.java)
    val dockerConfiguration: DockerConfiguration = objects.newInstance(DockerConfiguration::class)

    fun dockerConfiguration(configure: DockerConfiguration.() -> Unit) {
        dockerConfiguration.configure()
    }

    interface DockerConfiguration {
        val imageName: Property<String>
        val imageTag: Property<String>
        val imageRegistryUrl: Property<String>
        val imageRegistryUsername: Property<String>
        val imageRegistryPassword: Property<String>
    }
}
