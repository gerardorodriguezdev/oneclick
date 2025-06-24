plugins {
    id("theoneclick.jvm.library")
    alias(libs.plugins.kmp.atomicfu)
    alias(libs.plugins.ksp)
}

kotlin {
    sourceSets {
        jvmMain {
            dependencies {
                implementation(ktorLibs.server.core)
                implementation(ktorLibs.serialization.kotlinx.json)
                implementation(ktorLibs.server.auth)
                implementation(ktorLibs.server.contentNegotiation)
                implementation(ktorLibs.server.callLogging)
                implementation(ktorLibs.server.requestValidation)
                implementation(ktorLibs.server.statusPages)
                implementation(ktorLibs.server.rateLimit)
                implementation(ktorLibs.server.callId)
                implementation(ktorLibs.server.compression)
                implementation(libs.kmp.kotlin.inject)
                implementation(libs.jvm.bcrypt)
                implementation(projects.shared.contracts.core)
                implementation(projects.shared.timeProvider)

                project.dependencies.ksp(libs.ksp.kotlin.inject)
            }
        }
    }
}
