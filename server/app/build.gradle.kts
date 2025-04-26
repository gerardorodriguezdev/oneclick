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

dependencies {
    implementation(libs.kmp.ktor.server.core)
    implementation(libs.kmp.ktor.server.cio)
    implementation(projects.server.core)
}

fun ChamaleonExtension.propertyProvider(name: String): Provider<String> =
    provider { selectedEnvironment().jvmPlatform.propertyStringValue(name) }
