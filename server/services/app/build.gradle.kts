import app.cash.sqldelight.gradle.SqlDelightDatabase
import buildLogic.convention.tasks.createDockerComposeConfigTask.CreateDockerComposeConfigInput.PostgresDatabase
import buildLogic.convention.tasks.createDockerComposeConfigTask.CreateDockerComposeConfigInput.RedisDatabase

plugins {
    id("theoneclick.jvm.server")
    alias(libs.plugins.kmp.sqldelight)
    alias(libs.plugins.kmp.serialization)
    alias(libs.plugins.kmp.atomicfu)
    alias(libs.plugins.kmp.poko)
}

jvmServer {
    jvmTarget.set(libs.versions.jvm.api.get().toInt())
    mainClass.set("theoneclick.server.services.app.ApplicationKt")

    dockerConfiguration {
        imageName.set("app")
        imagePort.set(intProvider("IMAGE_PORT"))
        imageTag.set(stringProvider("IMAGE_TAG"))
        imageRegistryUrl.set(stringProvider("REGISTRY_LOCATION"))
        imageRegistryUsername.set(stringProvider("REGISTRY_USERNAME"))
        imageRegistryPassword.set(stringProvider("REGISTRY_PASSWORD"))
    }

    dockerComposeConfiguration {
        dockerExecutablePath.set("/usr/local/bin/docker")
        dockerComposeExecutablePath.set("/usr/local/bin/docker-compose")

        postgresDatabase.set(
            provider {
                PostgresDatabase(
                    imageVersion = libs.versions.docker.postgres.api.get().toInt(),
                    databaseName = stringProvider("PGDATABASE").get(),
                    databaseUsername = stringProvider("POSTGRES_USER").get(),
                    databasePassword = stringProvider("POSTGRES_PASSWORD").get(),
                    imagePort = stringProvider("PGPORT").get().toInt(),
                )
            }
        )

        redisDatabase.set(
            RedisDatabase(
                imageVersion = libs.versions.docker.redis.api.get().toInt(),
            )
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
    implementation(libs.jvm.ktor.opentelemetry)
    implementation(libs.jvm.opentelemetry.autoconfig)
    implementation(libs.jvm.opentelemetry.otpl)
    implementation(projects.shared.logging)
    implementation(projects.shared.contracts.core)
    implementation(projects.shared.contracts.auth)
    implementation(projects.shared.contracts.homes)
    implementation(projects.shared.timeProvider)
    implementation(projects.shared.dispatchers)
    implementation(projects.server.shared.core)
    implementation(projects.server.shared.auth)
    implementation(projects.server.shared.db)

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
                packageName.set("theoneclick.server.services.app.postgresql")
                dialect(libs.kmp.sqldelight.postgresql)
            }
        )
    }
}

fun stringProvider(name: String): Provider<String> =
    provider { chamaleon.selectedEnvironment().jvmPlatform.propertyStringValue(name) }

fun intProvider(name: String): Provider<Int> =
    stringProvider(name).map { value -> value.toInt() }
