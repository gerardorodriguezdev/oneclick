plugins {
    id("theoneclick.jvm.server")
    alias(libs.plugins.kmp.serialization)
}

jvmServer {
    jvmTarget.set(libs.versions.jvm.api.get().toInt())
    mainClass.set("theoneclick.server.mock.ApplicationKt")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kmp.ktor.serialization.kotlinx.json)
                implementation(libs.kmp.ktor.server.core)
                implementation(libs.kmp.ktor.server.content.negotiation)
                implementation(libs.kmp.ktor.server.cio)
                implementation(projects.shared.core)
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.jvm.logback.classic)
            }
        }
    }
}
