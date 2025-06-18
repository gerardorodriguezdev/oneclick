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
    implementation(libs.kmp.ktor.server.core)
    implementation(libs.kmp.ktor.server.netty)
    implementation(libs.kmp.ktor.serialization.kotlinx.json)
    implementation(libs.kmp.ktor.server.core)
    implementation(libs.kmp.ktor.server.content.negotiation)
    implementation(libs.kmp.ktor.server.call.logging)
    implementation(libs.kmp.ktor.server.request.validation)
    implementation(libs.kmp.ktor.server.status.pages)
    implementation(libs.kmp.ktor.server.rate.limit)
    implementation(libs.kmp.ktor.server.call.id)
    implementation(libs.kmp.ktor.server.compression)
    implementation(libs.kmp.datetime)
    implementation(libs.kmp.kotlin.inject)
    implementation(libs.jvm.bcrypt)
    implementation(libs.jvm.logback.classic)
    implementation(libs.jvm.ktor.server.auth)
    implementation(projects.shared.logging)
    implementation(projects.shared.contracts.core)
    implementation(projects.shared.timeProvider)
    implementation(projects.server.shared)

    testImplementation(libs.kmp.test.ktor.server.host)
    testImplementation(libs.kmp.ktor.client.core)
    testImplementation(libs.kmp.ktor.client.cio)
    testImplementation(libs.kmp.ktor.client.content.negotiation)
    testImplementation(libs.kmp.test)

    ksp(libs.ksp.kotlin.inject)
}

fun stringProvider(name: String): Provider<String> =
    provider { chamaleon.selectedEnvironment().jvmPlatform.propertyStringValue(name) }
