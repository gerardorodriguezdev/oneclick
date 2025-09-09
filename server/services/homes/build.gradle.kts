import buildLogic.convention.tasks.createDockerComposeConfigTask.CreateDockerComposeConfigInput.PostgresDatabase
import buildLogic.convention.tasks.createDockerComposeConfigTask.CreateDockerComposeConfigInput.RedisDatabase

plugins {
    id("theoneclick.jvm.server")
    alias(libs.plugins.kmp.sqldelight)
    alias(libs.plugins.kmp.serialization)
    alias(libs.plugins.gradle.ksp)
}

jvmServer {
    jvmTarget.set(libs.versions.jvm.api.get().toInt())
    mainClass.set("theoneclick.server.services.homes.ApplicationKt")

    dockerConfiguration {
        imageName.set("homes")
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
                    databaseName = "HomesDatabase",
                    databaseUsername = stringProvider("POSTGRES_USERNAME").get(),
                    databasePassword = stringProvider("POSTGRES_PASSWORD").get(),
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
    implementation(ktorLibs.server.auth)
    implementation(ktorLibs.server.auth.jwt)
    implementation(libs.kmp.kotlin.inject)
    implementation(libs.kmp.sqldelight)
    implementation(libs.jvm.logback.classic)
    implementation(libs.jvm.hiraki)
    implementation(libs.jvm.redis)
    implementation(libs.jvm.postgresql)
    implementation(projects.shared.logging)
    implementation(projects.shared.contracts.core)
    implementation(projects.shared.timeProvider)
    implementation(projects.shared.dispatchers)
    implementation(projects.server.shared)

    testImplementation(ktorLibs.server.testHost)
    testImplementation(ktorLibs.client.core)
    testImplementation(ktorLibs.client.cio)
    testImplementation(ktorLibs.client.contentNegotiation)
    testImplementation(libs.kmp.test)

    ksp(libs.gradle.ksp.kotlin.inject)
}

sqldelight {
    databases {
        create("HomesDatabase") {
            packageName.set("theoneclick.server.services.homes.postgresql")
            dialect(libs.kmp.sqldelight.postgresql)
        }
    }
}

fun stringProvider(name: String): Provider<String> =
    provider { chamaleon.selectedEnvironment().jvmPlatform.propertyStringValue(name) }

fun intProvider(name: String): Provider<Int> =
    stringProvider(name).map { value -> value.toInt() }
