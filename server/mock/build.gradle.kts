plugins {
    id("theoneclick.jvm.server")
    alias(libs.plugins.kmp.serialization)
}

jvmServer {
    jvmTarget.set(libs.versions.jvm.api.get().toInt())
    mainClass.set("theoneclick.server.mock.ApplicationKt")

    dockerConfiguration {
        imageName.set("mock")
        imagePort.set(intProvider("IMAGE_PORT"))
        imageTag.set(stringProvider("IMAGE_TAG"))
        imageRegistryUrl.set(stringProvider("REGISTRY_LOCATION"))
        imageRegistryUsername.set(stringProvider("REGISTRY_USERNAME"))
        imageRegistryPassword.set(stringProvider("REGISTRY_PASSWORD"))
    }

    dockerComposeConfiguration {
        dockerExecutablePath.set("/usr/local/bin/docker")
        dockerComposeExecutablePath.set("/usr/local/bin/docker-compose")
    }
}

dependencies {
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.contentNegotiation)
    implementation(ktorLibs.server.netty)
    implementation(ktorLibs.serialization.kotlinx.json)
    implementation(projects.shared.contracts.core)
    implementation(projects.server.shared)
    implementation(libs.jvm.logback.classic)
}

fun stringProvider(name: String): Provider<String> =
    provider { chamaleon.selectedEnvironment().jvmPlatform.propertyStringValue(name) }

fun intProvider(name: String): Provider<Int> =
    stringProvider(name).map { value -> value.toInt() }