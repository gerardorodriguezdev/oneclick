plugins {
    id("theoneclick.jvm.library")
    alias(libs.plugins.kmp.serialization)
    alias(libs.plugins.kmp.atomicfu)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kmp.ktor.serialization.kotlinx.json)
                implementation(libs.kmp.ktor.server.core)
                implementation(libs.kmp.ktor.server.content.negotiation)
                implementation(libs.kmp.ktor.server.call.logging)
                implementation(libs.kmp.ktor.server.request.validation)
                implementation(libs.kmp.ktor.server.status.pages)
                implementation(libs.kmp.ktor.server.rate.limit)
                implementation(libs.kmp.ktor.server.call.id)
                implementation(libs.kmp.datetime)
                implementation(libs.kmp.ktor.server.cio)
                implementation(libs.kmp.koin.core)
                implementation(projects.shared.base)
                implementation(projects.shared.timeProvider)
                implementation(projects.server.shared)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kmp.test.ktor.server.host)
                implementation(libs.kmp.test.koin)
                implementation(libs.kmp.ktor.client.core)
                implementation(libs.kmp.ktor.client.cio)
                implementation(libs.kmp.ktor.client.content.negotiation)
                implementation(libs.kmp.test)
                implementation(projects.shared.testing)
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.jvm.bcrypt)
                implementation(libs.jvm.logback.classic)
                implementation(libs.jvm.ktor.server.auth)
            }
        }
    }
}
