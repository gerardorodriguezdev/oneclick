plugins {
    id("oneclick.jvm.server")
    alias(libs.plugins.kmp.serialization)
}

jvmServer {
    jvmTarget = libs.versions.jvm.api.get().toInt()
    mainClass = "oneclick.server.services.mock.ApplicationKt"

    dockerConfiguration {
        imageName = "mock"
        imagePort = intProvider("IMAGE_PORT")
        imageTag = stringProvider("IMAGE_TAG")
        imageRegistryUrl = stringProvider("REGISTRY_LOCATION")
        imageRegistryUsername = stringProvider("REGISTRY_USERNAME")
        imageRegistryPassword = stringProvider("REGISTRY_PASSWORD")
    }

    dockerComposeConfiguration {
        dockerExecutablePath = "/usr/local/bin/docker"
        dockerComposeExecutablePath = "/usr/local/bin/docker-compose"
    }
}

dependencies {
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.contentNegotiation)
    implementation(ktorLibs.server.netty)
    implementation(ktorLibs.serialization.kotlinx.json)
    implementation(projects.shared.contracts.core)
    implementation(projects.shared.contracts.auth)
    implementation(projects.shared.contracts.homes)
    implementation(projects.server.shared.utils)
    implementation(libs.jvm.logback.classic)
}

fun stringProvider(name: String): Provider<String> =
    provider { chamaleon.selectedEnvironment().jvmPlatform.propertyStringValue(name) }

fun intProvider(name: String): Provider<Int> =
    stringProvider(name).map { value -> value.toInt() }
