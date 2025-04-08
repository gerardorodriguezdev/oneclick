plugins {
    id("theoneclick.jvm.library")
}

jvmLibrary {
    jvmTarget.set(libs.versions.jvm.api.get().toInt())
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kmp.ktor.serialization.kotlinx.json)
                implementation(libs.kmp.ktor.server.core)
                implementation(libs.kmp.ktor.server.content.negotiation)
                implementation(libs.kmp.ktor.server.cio)
                implementation(projects.shared.base)
            }
        }
    }
}
