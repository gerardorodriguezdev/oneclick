plugins {
    id("oneclick.jvm.library")
    alias(libs.plugins.kmp.serialization)
    alias(libs.plugins.kmp.poko)
}

kotlin {
    sourceSets {
        jvmMain {
            dependencies {
                implementation(libs.jvm.bcrypt)
                implementation(ktorLibs.server.auth)
                implementation(ktorLibs.server.auth.jwt)
                implementation(ktorLibs.serialization.kotlinx.json)
                implementation(projects.shared.timeProvider)
                implementation(projects.shared.dispatchers)
                implementation(projects.shared.contracts.core)
                implementation(projects.shared.contracts.auth)
                runtimeOnly(libs.kmp.poko)
            }
        }
    }
}
