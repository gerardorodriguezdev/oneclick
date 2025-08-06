package buildLogic.convention.tasks.createDockerComposeConfigTask

import kotlinx.serialization.Serializable
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional

data class CreateDockerComposeConfigInput(
    @get:Nested
    val app: App,

    @Optional
    @get:Nested
    val postgresDatabase: PostgresDatabase?,

    @Optional
    @get:Nested
    val redisDatabase: RedisDatabase?
) {
    data class App(
        @get:Input
        val imageName: String,
        @get:Input
        val imageTag: String,
        @get:Input
        val imagePort: Int,
        @get:Input
        val environmentVariables: Map<String, String>,
    )

    data class PostgresDatabase(
        @get:Input
        val imageVersion: Int,
        @get:Input
        val imagePort: Int = 5432,
        @get:Input
        val imageVolume: String = "/var/lib/postgresql/data",
        @get:Input
        val databaseName: String,
        @get:Input
        val databaseUsername: String,
        @get:Input
        val databasePassword: String,
    )

    @Serializable
    data class RedisDatabase(
        @get:Input
        val imageVersion: Int,
        @get:Input
        val imagePort: Int = 6379,
        @get:Input
        val imageVolume: String = "/data",
    )
}