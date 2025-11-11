import app.cash.sqldelight.gradle.SqlDelightDatabase
import buildLogic.convention.extensions.consumeWasmWebsite
import buildLogic.convention.tasks.createDockerComposeConfigTask.CreateDockerComposeConfigInput.PostgresDatabase
import buildLogic.convention.tasks.createDockerComposeConfigTask.CreateDockerComposeConfigInput.RedisDatabase

plugins {
    id("oneclick.jvm.server")
    alias(libs.plugins.kmp.sqldelight)
    alias(libs.plugins.kmp.serialization)
    alias(libs.plugins.kmp.poko)
}

jvmServer {
    jvmTarget = libs.versions.jvm.api.get().toInt()
    mainClass = "oneclick.server.services.app.ApplicationKt"

    dockerConfiguration {
        imageName = stringProvider("IMAGE_NAME")
        imagePort = intProvider("IMAGE_PORT")
        imageTag = stringProvider("IMAGE_TAG")
        imageRegistryUrl = stringProvider("REGISTRY_LOCATION")
        imageRegistryUsername = stringProvider("REGISTRY_USERNAME")
        imageRegistryPassword = stringProvider("REGISTRY_PASSWORD")
    }

    dockerComposeConfiguration {
        dockerExecutablePath = "/usr/local/bin/docker"
        dockerComposeExecutablePath = "/usr/local/bin/docker-compose"

        postgresDatabase =
            provider {
                PostgresDatabase(
                    imageVersion = libs.versions.docker.postgres.api.get().toInt(),
                    databaseName = stringProvider("POSTGRES_DATABASE").get(),
                    databaseUsername = stringProvider("POSTGRES_USERNAME").get(),
                    databasePassword = stringProvider("POSTGRES_PASSWORD").get(),
                )
            }

        redisDatabase = RedisDatabase(
            imageVersion = libs.versions.docker.redis.api.get().toInt(),
        )
    }
}

dependencies {
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.netty)
    implementation(ktorLibs.serialization.kotlinx.json)
    implementation(ktorLibs.server.contentNegotiation)
    implementation(ktorLibs.server.callLogging)
    implementation(ktorLibs.server.requestValidation)
    implementation(ktorLibs.server.statusPages)
    implementation(ktorLibs.server.rateLimit)
    implementation(ktorLibs.server.callId)
    implementation(ktorLibs.server.compression)
    implementation(ktorLibs.server.auth)
    implementation(ktorLibs.server.auth.jwt)
    implementation(ktorLibs.server.sessions)
    implementation(libs.kmp.sqldelight)
    implementation(libs.jvm.logback.classic)
    implementation(libs.jvm.hiraki)
    implementation(libs.jvm.redis)
    implementation(libs.jvm.reactive)
    implementation(libs.jvm.postgresql)
    implementation(projects.shared.logging)
    implementation(projects.shared.contracts.core)
    implementation(projects.shared.contracts.auth)
    implementation(projects.shared.contracts.homes)
    implementation(projects.shared.timeProvider)
    implementation(projects.shared.dispatchers)
    implementation(projects.shared.security)
    implementation(projects.server.shared.utils)
    implementation(projects.server.shared.auth)
    implementation(projects.server.shared.db)
    consumeWasmWebsite(projects.client.apps.user.core.path)

    testImplementation(ktorLibs.server.testHost)
    testImplementation(ktorLibs.client.core)
    testImplementation(ktorLibs.client.cio)
    testImplementation(ktorLibs.client.contentNegotiation)
    testImplementation(libs.kmp.test)

    runtimeOnly(libs.kmp.poko)
}

sqldelight {
    databases {
        create(
            name = "AppDatabase",
            configureAction = Action<SqlDelightDatabase> {
                packageName = "oneclick.server.services.app.postgresql"
                dialect(libs.kmp.sqldelight.postgresql)
            }
        )
    }
}

fun stringProvider(name: String): Provider<String> =
    provider { chamaleon.selectedEnvironment().jvmPlatform.propertyStringValue(name) }

fun intProvider(name: String): Provider<Int> =
    stringProvider(name).map { value -> value.toInt() }
