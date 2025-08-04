package buildLogic.convention.extensions.plugins

import buildLogic.convention.tasks.createDockerComposeConfigTask.CreateDockerComposeConfigInput.PostgresDatabase
import buildLogic.convention.tasks.createDockerComposeConfigTask.CreateDockerComposeConfigInput.RedisDatabase
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

open class JvmServerExtension @Inject constructor(objects: ObjectFactory) {
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

    interface DockerConfiguration {
        val imagePort: Property<Int>
        val imageName: Property<String>
        val imageTag: Property<String>
        val imageRegistryUrl: Property<String>
        val imageRegistryUsername: Property<String>
        val imageRegistryPassword: Property<String>
    }

    interface DockerComposeConfiguration {
        val dockerExecutablePath: Property<String>
        val dockerComposeExecutablePath: Property<String>
        val postgresDatabase: Property<PostgresDatabase>
        val redisDatabase: Property<RedisDatabase>
    }
}
