plugins {
    id("theoneclick.jvm.server")
    alias(libs.plugins.kmp.serialization)
    alias(libs.plugins.kmp.atomicfu)
    alias(libs.plugins.ksp)
}

jvmServer {
    jvmTarget.set(libs.versions.jvm.api.get().toInt())
    mainClass.set("theoneclick.server.app.ApplicationKt")

    dockerConfiguration {
        imageName.set("theoneclick")
        imageTag.set(stringProvider("GITHUB_SHA"))
        imageRegistryUrl.set(stringProvider("REGISTRY_LOCATION"))
        imageRegistryUsername.set(stringProvider("REGISTRY_USERNAME"))
        imageRegistryPassword.set(stringProvider("REGISTRY_PASSWORD"))
    }
}

dependencies {
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.netty)
    implementation(ktorLibs.server.contentNegotiation)
    implementation(ktorLibs.server.callLogging)
    implementation(ktorLibs.server.requestValidation)
    implementation(ktorLibs.server.statusPages)
    implementation(ktorLibs.server.rateLimit)
    implementation(ktorLibs.server.callId)
    implementation(ktorLibs.server.compression)
    implementation(ktorLibs.server.auth)
    implementation(ktorLibs.serialization.kotlinx.json)
    implementation(libs.kmp.datetime)
    implementation(libs.kmp.kotlin.inject)
    implementation(libs.jvm.bcrypt)
    implementation(libs.jvm.logback.classic)
    implementation(projects.shared.logging)
    implementation(projects.shared.contracts.core)
    implementation(projects.shared.timeProvider)
    implementation(projects.server.shared)

    testImplementation(ktorLibs.server.testHost)
    testImplementation(ktorLibs.client.core)
    testImplementation(ktorLibs.client.cio)
    testImplementation(ktorLibs.client.contentNegotiation)
    testImplementation(libs.kmp.test)

    ksp(libs.ksp.kotlin.inject)
}

fun stringProvider(name: String): Provider<String> =
    provider { chamaleon.selectedEnvironment().jvmPlatform.propertyStringValue(name) }
