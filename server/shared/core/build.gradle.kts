plugins {
    id("theoneclick.jvm.library")
    alias(libs.plugins.kmp.atomicfu)
    alias(libs.plugins.kmp.serialization)
    alias(libs.plugins.gradle.ksp)
    alias(libs.plugins.kmp.poko)
}

kotlin {
    sourceSets {
        jvmMain {
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
                implementation(libs.kmp.kotlin.inject)
                implementation(libs.jvm.hiraki)
                implementation(libs.kmp.sqldelight)
                implementation(projects.shared.contracts.core)
                implementation(projects.server.shared.auth)
                implementation(projects.shared.contracts.auth)
                implementation(projects.shared.timeProvider)
                implementation(projects.shared.dispatchers)

                runtimeOnly(libs.kmp.poko)

                project.dependencies.kspJvm(libs.gradle.ksp.kotlin.inject)
            }
        }
    }
}
