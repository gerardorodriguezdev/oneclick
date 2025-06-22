plugins {
    id("theoneclick.jvm.server")
    alias(libs.plugins.kmp.serialization)
}

jvmServer {
    jvmTarget.set(libs.versions.jvm.api.get().toInt())
    mainClass.set("theoneclick.server.mock.ApplicationKt")
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
