package buildLogic.convention.tasks.createDockerComposeConfigTask

import buildLogic.convention.tasks.createDockerComposeConfigTask.CreateDockerComposeConfigTask.DockerComposeFile.Service
import buildLogic.convention.tasks.createDockerComposeConfigTask.CreateDockerComposeConfigTask.DockerComposeFile.Volumes
import com.charleskorn.kaml.SequenceStyle
import com.charleskorn.kaml.SingleLineStringStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class CreateDockerComposeConfigTask : DefaultTask() {

    @get:Nested
    abstract val input: Property<CreateDockerComposeConfigInput>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun createFile() {
        val outputFile = outputFile.get().asFile
        outputFile.writeText(
            dockerComposeConfigContent(input.get())
        )
    }

    private fun dockerComposeConfigContent(input: CreateDockerComposeConfigInput): String =
        yaml.encodeToString(
            DockerComposeFile.serializer(),
            DockerComposeFile(
                services = buildMap {
                    put(
                        key = APP_IMAGE_NAME,
                        value = appService(
                            imageName = input.app.imageName,
                            imageTag = input.app.imageTag,
                            imagePort = input.app.imagePort,
                            environmentVariables = input.app.environmentVariables,
                            dependsOnPostgresDb = input.postgresDatabase != null,
                            dependsOnRedisDb = input.redisDatabase != null,
                        )
                    )

                    input.postgresDatabase?.let { postgresDatabase ->
                        put(
                            key = POSTGRES_IMAGE_NAME,
                            value = postgresService(
                                databaseName = postgresDatabase.databaseName,
                                imageVersion = postgresDatabase.imageVersion.toString(),
                                imagePort = postgresDatabase.imagePort,
                                databaseUsername = postgresDatabase.databaseUsername,
                                databasePassword = postgresDatabase.databasePassword,
                                imageVolume = postgresDatabase.imageVolume,
                            )
                        )
                    }

                    input.redisDatabase?.let { redisDatabase ->
                        put(
                            key = REDIS_IMAGE_NAME,
                            value = redisService(
                                imageVersion = redisDatabase.imageVersion.toString(),
                                imagePort = redisDatabase.imagePort,
                                imageVolume = redisDatabase.imageVolume,
                            )
                        )
                    }
                },
                volumes = Volumes()
            )
        )

    private fun appService(
        imageName: String,
        imageTag: String,
        imagePort: Int,
        environmentVariables: Map<String, String>,
        dependsOnPostgresDb: Boolean,
        dependsOnRedisDb: Boolean,
    ): Service =
        Service(
            image = image(
                imageName = imageName,
                imageTag = imageTag
            ),
            ports = ports(imagePort),
            environment = environmentVariables,
            dependsOn = buildList {
                if (dependsOnPostgresDb) add(POSTGRES_IMAGE_NAME)
                if (dependsOnRedisDb) add(REDIS_IMAGE_NAME)
            },
        )

    private fun postgresService(
        databaseName: String,
        databaseUsername: String,
        databasePassword: String,
        imageVersion: String,
        imagePort: Int,
        imageVolume: String,
    ): Service =
        Service(
            image = image(
                imageName = POSTGRES_IMAGE_NAME,
                imageTag = imageVersion
            ),
            ports = ports(imagePort),
            environment = buildMap {
                put("POSTGRES_DB", databaseName)
                put("POSTGRES_USER", databaseUsername)
                put("POSTGRES_PASSWORD", databasePassword)
            },
            volumes = listOf("$POSTGRES_VOLUME_NAME:$imageVolume"),
        )

    private fun redisService(
        imageVersion: String,
        imagePort: Int,
        imageVolume: String,
    ): Service =
        Service(
            image = image(
                imageName = REDIS_IMAGE_NAME,
                imageTag = imageVersion
            ),
            ports = ports(imagePort),
            volumes = listOf("$REDIS_VOLUME_NAME:$imageVolume"),
        )

    private fun image(imageName: String, imageTag: String): String = "$imageName:$imageTag"

    private fun ports(port: Int): List<String> = listOf("$port:$port")

    private companion object {
        val yaml = Yaml(
            configuration = YamlConfiguration(
                sequenceStyle = SequenceStyle.Flow,
                singleLineStringStyle = SingleLineStringStyle.PlainExceptAmbiguous,
            )
        )
        const val APP_IMAGE_NAME = "app"
        const val POSTGRES_IMAGE_NAME = "postgres"
        const val REDIS_IMAGE_NAME = "redis"

        const val POSTGRES_VOLUME_NAME = "postgres_data"
        const val REDIS_VOLUME_NAME = "redis_data"
    }

    @Serializable
    private data class DockerComposeFile(
        val services: Map<String, Service>,
        val volumes: Volumes,
    ) {
        @Serializable
        data class Service(
            val image: String,
            val ports: List<String>,
            val environment: Map<String, String> = emptyMap(),
            @SerialName("depends_on")
            val dependsOn: List<String> = emptyList(),
            val volumes: List<String> = emptyList(),
        )

        @Serializable
        data class Volumes(
            @SerialName(POSTGRES_VOLUME_NAME)
            val progresData: String? = null,
            @SerialName(REDIS_VOLUME_NAME)
            val redisData: String? = null,
        )
    }
}