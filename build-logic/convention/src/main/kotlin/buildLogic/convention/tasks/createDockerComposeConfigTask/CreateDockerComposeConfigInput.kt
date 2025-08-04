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
        val port: Int,
        @get:Input
        val environmentVariables: Map<String, String>,
    )

    data class PostgresDatabase(
        @get:Input
        val imageVersion: Int,
        @get:Input
        val databaseName: String,
        @get:Input
        val username: String,
        @Optional
        @get:Input
        val password: String?,
        @get:Input
        val port: Int,
    )

    @Serializable
    data class RedisDatabase(
        @get:Input
        val imageVersion: Int,
        @get:Input
        val port: Int,
    )
}