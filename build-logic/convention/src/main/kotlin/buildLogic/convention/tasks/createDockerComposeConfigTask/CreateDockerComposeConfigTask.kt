package buildLogic.convention.tasks.createDockerComposeConfigTask

import com.charleskorn.kaml.Yaml
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
        Yaml.default.encodeToString(
            DockerComposeFile.serializer(),
            DockerComposeFile(
                services = DockerComposeFile.Services(
                    app = appService(
                        imageName = input.app.imageName,
                        imageTag = input.app.imageTag,
                        imagePort = input.app.imagePort,
                        environmentVariables = input.app.environmentVariables,
                        dependsOnPostgresDb = input.postgresDatabase != null,
                        dependsOnRedisDb = input.redisDatabase != null,
                    ),
                    postgres = input.postgresDatabase?.let { postgresDatabase ->
                        postgresService(
                            databaseName = postgresDatabase.databaseName,
                            imageVersion = postgresDatabase.imageVersion.toString(),
                            imagePort = postgresDatabase.imagePort,
                            databaseUsername = postgresDatabase.databaseUsername,
                            databasePassword = postgresDatabase.databasePassword,
                            imageVolume = postgresDatabase.imageVolume,
                        )
                    },
                    redis = input.redisDatabase?.let { redisDatabase ->
                        redisService(
                            imageVersion = redisDatabase.imageVersion.toString(),
                            imagePort = redisDatabase.imagePort,
                            imageVolume = redisDatabase.imageVolume,
                        )
                    },
                ),
                volumes = DockerComposeFile.Volumes()
            )
        )

    private fun appService(
        imageName: String,
        imageTag: String,
        imagePort: Int,
        environmentVariables: Map<String, String>,
        dependsOnPostgresDb: Boolean,
        dependsOnRedisDb: Boolean,
    ): DockerComposeFile.Services.App =
        DockerComposeFile.Services.App(
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
        databasePassword: String?,
        imageVersion: String,
        imagePort: Int,
        imageVolume: String,
    ): DockerComposeFile.Services.Postgres =
        DockerComposeFile.Services.Postgres(
            image = image(
                imageName = POSTGRES_IMAGE_NAME,
                imageTag = imageVersion
            ),
            ports = ports(imagePort),
            environment = buildMap {
                put("POSTGRES_DB", databaseName)
                put("POSTGRES_USER", databaseUsername)
                databasePassword?.let { put("POSTGRES_PASSWORD", databasePassword) }
            },
            volumes = listOf("postgres_data:$imageVolume"),
        )

    private fun redisService(
        imageVersion: String,
        imagePort: Int,
        imageVolume: String,
    ): DockerComposeFile.Services.Redis =
        DockerComposeFile.Services.Redis(
            image = image(
                imageName = REDIS_IMAGE_NAME,
                imageTag = imageVersion
            ),
            ports = ports(imagePort),
            volumes = listOf("redis_data:$imageVolume"),
        )

    private fun image(imageName: String, imageTag: String): String = "$imageName:$imageTag"

    private fun ports(port: Int): List<String> = listOf("$port:$port")

    private companion object {
        const val POSTGRES_IMAGE_NAME = "postgres"
        const val REDIS_IMAGE_NAME = "redis"

        const val POSTGRES_VOLUME_NAME = "postgres_data"
        const val REDIS_VOLUME_NAME = "redis_data"
    }

    @Serializable
    private data class DockerComposeFile(
        val services: Services,
        val volumes: Volumes,
    ) {
        @Serializable
        data class Services(
            val app: App,
            val postgres: Postgres?,
            val redis: Redis?,
        ) {
            @Serializable
            data class App(
                val image: String,
                val ports: List<String>,
                val environment: Map<String, String>,
                @SerialName("depends_on")
                val dependsOn: List<String> = emptyList(),
            )

            @Serializable
            data class Postgres(
                val image: String,
                val ports: List<String>,
                val environment: Map<String, String>,
                val volumes: List<String>,
            )

            @Serializable
            data class Redis(
                val image: String,
                val ports: List<String>,
                val volumes: List<String>,
            )
        }

        @Serializable
        data class Volumes(
            @SerialName(POSTGRES_VOLUME_NAME)
            val progresData: String? = null,
            @SerialName(REDIS_VOLUME_NAME)
            val redisData: String? = null,
        )
    }
}