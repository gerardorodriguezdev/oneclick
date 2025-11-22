package buildLogic.convention.extensions.plugins

import buildLogic.convention.models.DockerComposeConfiguration
import buildLogic.convention.models.DockerConfiguration
import buildLogic.convention.models.ImageConfiguration
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

open class JvmServerExtension @Inject constructor(private val objects: ObjectFactory) {
    val jvmTarget: Property<Int> = objects.property(Int::class.java)
    val mainClass: Property<String> = objects.property(String::class.java)
    val dockerConfiguration: DockerConfiguration = objects.newInstance(DockerConfiguration::class)
    val dockerComposeConfiguration: DockerComposeConfiguration = objects.newInstance(DockerComposeConfiguration::class)

    fun dockerConfiguration(configure: DockerConfiguration.() -> Unit) {
        dockerConfiguration.configure()
    }

    fun dockerComposeConfiguration(configure: DockerComposeConfiguration.() -> Unit) {
        dockerComposeConfiguration.configure()
    }

    fun DockerComposeConfiguration.postgres(
        imageVersion: Int,
        port: Int = 5432,
        volume: String = "/var/lib/postgresql/data",
        databaseName: String,
        databaseUsername: String,
        databasePassword: String,
    ) {
        val imageConfiguration = objects.newInstance(ImageConfiguration::class)
        dockerComposeConfiguration.imagesConfigurations.add(
            imageConfiguration.apply {
                name.set("postgres")
                tag.set(imageVersion.toString())
                this.port.set(port)
                this.volume.set(volume)
                environmentVariables.put("POSTGRES_DB", databaseName)
                environmentVariables.put("POSTGRES_USER", databaseUsername)
                environmentVariables.put("POSTGRES_PASSWORD", databasePassword)
            }
        )
    }

    fun DockerComposeConfiguration.redis(
        imageVersion: Int,
        port: Int = 6379,
        volume: String = "/data",
    ) {
        val imageConfiguration = objects.newInstance(ImageConfiguration::class)
        dockerComposeConfiguration.imagesConfigurations.add(
            imageConfiguration.apply {
                name.set("redis")
                tag.set(imageVersion.toString())
                this.port.set(port)
                this.volume.set(volume)
            }
        )
    }
}
