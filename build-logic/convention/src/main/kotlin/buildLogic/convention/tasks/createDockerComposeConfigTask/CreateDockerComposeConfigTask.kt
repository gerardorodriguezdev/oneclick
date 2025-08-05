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

    @get: OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun createFile() {
        val outputFile = outputFile.get().asFile
        outputFile.writeText(
            dockerComposeConfigContent(input.get())
        )
    }

    //TODO: Add missing configs + healthchecks
    //TODO: Move volumes out
    //TODO: Fix postgres trusted
    private fun dockerComposeConfigContent(input: CreateDockerComposeConfigInput): String =
        Yaml.default.encodeToString(
            DockerComposeFile.serializer(),
            DockerComposeFile(
                services = DockerComposeFile.Services(
                    app = appService(
                        imageName = input.app.imageName,
                        imageTag = input.app.imageTag,
                        port = input.app.port,
                        environmentVariables = input.app.environmentVariables,
                        dependsOnPostgresDb = input.postgresDatabase != null,
                        dependsOnRedisDb = input.redisDatabase != null,
                    ),
                    postgres = input.postgresDatabase?.let { postgresDatabase ->
                        postgresService(
                            databaseName = postgresDatabase.databaseName,
                            imageVersion = postgresDatabase.imageVersion.toString(),
                            port = postgresDatabase.port,
                        )
                    },
                    redis = input.redisDatabase?.let { redisDatabase ->
                        redisService(
                            imageVersion = redisDatabase.imageVersion.toString(),
                            port = redisDatabase.port,
                        )
                    },
                ),
                volumes = DockerComposeFile.Volumes()
            )
        )

    private fun appService(
        imageName: String,
        imageTag: String,
        port: Int,
        environmentVariables: Map<String, String>,
        dependsOnPostgresDb: Boolean,
        dependsOnRedisDb: Boolean,
    ): DockerComposeFile.Services.App =
        DockerComposeFile.Services.App(
            image = image(
                imageName = imageName,
                imageTag = imageTag
            ),
            ports = ports(port),
            environment = environmentVariables,
            dependsOn = buildList {
                if (dependsOnPostgresDb) add("postgres")
                if (dependsOnRedisDb) add("redis")
            },
        )

    private fun postgresService(
        databaseName: String,
        imageVersion: String,
        port: Int,
    ): DockerComposeFile.Services.Postgres =
        DockerComposeFile.Services.Postgres(
            image = image(
                imageName = "postgres",
                imageTag = imageVersion
            ),
            ports = ports(port),
            environment = buildMap {
                put("POSTGRES_DB", databaseName)
                put("POSTGRES_HOST_AUTH_METHOD", "trust")
            },
            volumes = listOf("postgres_data:/var/lib/postgresql/data"),
        )

    private fun redisService(
        imageVersion: String,
        port: Int,
    ): DockerComposeFile.Services.Redis =
        DockerComposeFile.Services.Redis(
            image = image(
                imageName = "redis",
                imageTag = imageVersion
            ),
            ports = ports(port),
            volumes = listOf("redis_data:/data"),
        )

    private fun image(imageName: String, imageTag: String): String = "$imageName:$imageTag"

    private fun ports(port: Int): List<String> = listOf("$port:$port")

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
            @SerialName("postgres_data")
            val progresData: String? = null,
            @SerialName("redis_data")
            val redisData: String? = null,
        )
    }
}