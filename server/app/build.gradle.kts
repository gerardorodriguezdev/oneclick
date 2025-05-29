import io.github.gerardorodriguezdev.chamaleon.gradle.plugin.extensions.ChamaleonExtension

plugins {
    id("theoneclick.jvm.server")
    alias(libs.plugins.kmp.serialization)
    alias(libs.plugins.kmp.atomicfu)
}

jvmServer {
    jvmTarget.set(libs.versions.jvm.api.get().toInt())
    mainClass.set("theoneclick.server.app.ApplicationKt")

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
    implementation(libs.kmp.ktor.serialization.kotlinx.json)
    implementation(libs.kmp.ktor.server.core)
    implementation(libs.kmp.ktor.server.content.negotiation)
    implementation(libs.kmp.ktor.server.call.logging)
    implementation(libs.kmp.ktor.server.request.validation)
    implementation(libs.kmp.ktor.server.status.pages)
    implementation(libs.kmp.ktor.server.rate.limit)
    implementation(libs.kmp.ktor.server.call.id)
    implementation(libs.kmp.ktor.server.cio)
    implementation(libs.kmp.datetime)
    implementation(libs.kmp.koin.core)
    implementation(libs.kmp.koin.ktor)
    implementation(libs.jvm.bcrypt)
    implementation(libs.jvm.logback.classic)
    implementation(libs.jvm.ktor.server.auth)
    implementation(projects.shared.base)
    implementation(projects.shared.timeProvider)
    implementation(projects.server.shared)

    testImplementation(libs.kmp.test.ktor.server.host)
    testImplementation(libs.kmp.test.koin)
    testImplementation(libs.kmp.ktor.client.core)
    testImplementation(libs.kmp.ktor.client.cio)
    testImplementation(libs.kmp.ktor.client.content.negotiation)
    testImplementation(libs.kmp.test)
    testImplementation(projects.shared.testing)
}

fun ChamaleonExtension.propertyProvider(name: String): Provider<String> =
    provider { selectedEnvironment().jvmPlatform.propertyStringValue(name) }
