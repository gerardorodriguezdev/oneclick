import io.github.gerardorodriguezdev.chamaleon.gradle.plugin.extensions.ChamaleonExtension

plugins {
    id("theoneclick.jvm.server")
    alias(libs.plugins.kmp.serialization)
    alias(libs.plugins.kmp.atomicfu)
}

jvmServer {
    jvmTarget.set(libs.versions.jvm.api.get().toInt())
    mainClass.set("theoneclick.server.core.ApplicationKt")

    dockerConfiguration {
        imageName.set("theoneclick")
        imageTag.set(chamaleon.propertyProvider("GITHUB_SHA"))
        imageRegistryUrl.set(chamaleon.propertyProvider("REGISTRY_LOCATION"))
        imageRegistryUsername.set(chamaleon.propertyProvider("REGISTRY_USERNAME"))
        imageRegistryPassword.set(chamaleon.propertyProvider("REGISTRY_PASSWORD"))
    }
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kmp.ktor.serialization.kotlinx.json)
                implementation(libs.kmp.ktor.server.core)
                implementation(libs.kmp.ktor.server.content.negotiation)
                implementation(libs.kmp.ktor.server.call.logging)
                implementation(libs.kmp.ktor.server.request.validation)
                implementation(libs.kmp.ktor.server.status.pages)
                implementation(libs.kmp.ktor.server.rate.limit)
                implementation(libs.kmp.ktor.server.call.id)
                implementation(libs.kmp.datetime)
                implementation(libs.kmp.ktor.server.cio)
                implementation(libs.kmp.koin.core)
                implementation(projects.shared.core)
                implementation(projects.shared.timeProvider)
                implementation(projects.server.shared)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kmp.test.ktor.server.host)
                implementation(libs.kmp.test.koin)
                implementation(libs.kmp.ktor.client.core)
                implementation(libs.kmp.ktor.client.cio)
                implementation(libs.kmp.ktor.client.content.negotiation)
                implementation(libs.kmp.test)
                implementation(projects.shared.testing)
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.jvm.bcrypt)
                implementation(libs.jvm.logback.classic)
                implementation(libs.jvm.ktor.server.auth)
            }
        }
    }
}

fun ChamaleonExtension.propertyProvider(name: String): Provider<String> =
    provider { selectedEnvironment().jvmPlatform.propertyStringValue(name) }