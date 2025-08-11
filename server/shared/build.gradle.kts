plugins {
    id("theoneclick.jvm.library")
    alias(libs.plugins.kmp.atomicfu)
    alias(libs.plugins.kmp.sqldelight)
    alias(libs.plugins.kmp.serialization)
    alias(libs.plugins.gradle.ksp)
    alias(libs.plugins.kmp.poko)
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
                implementation(ktorLibs.server.sessions)
                implementation(ktorLibs.server.auth.jwt)
                implementation(libs.kmp.kotlin.inject)
                implementation(libs.kmp.sqldelight)
                implementation(libs.kmp.sqldelight.coroutines)
                implementation(libs.jvm.bcrypt)
                implementation(libs.jvm.redis)
                implementation(libs.jvm.reactive)
                implementation(libs.jvm.kafka)
                implementation(projects.shared.contracts.core)
                implementation(projects.shared.timeProvider)
                implementation(projects.shared.dispatchers)
                runtimeOnly(libs.kmp.poko)

                project.dependencies.kspJvm(libs.gradle.ksp.kotlin.inject)
            }
        }
    }
}

sqldelight {
    databases {
        create("SharedDatabase") {
            packageName.set("theoneclick.server.shared.postgresql")
            dialect(libs.kmp.sqldelight.postgresql)
        }
    }
}
