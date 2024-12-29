plugins {
    id("theoneclick.jvm.library")
    id("theoneclick.wasm.library")
}

jvmLibrary {
    jvmTarget.set(libs.versions.jvm.api.get().toInt())
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kmp.test)
                implementation(libs.kmp.coroutines)
                implementation(libs.kmp.ktor.client.content.negotiation)
                implementation(libs.kmp.test.coroutines)
                implementation(libs.kmp.test.ktor.client.mock)
                implementation(libs.kmp.ktor.serialization.kotlinx.json)
                implementation(projects.shared.dispatchers)
                implementation(projects.shared.timeProvider)
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.jvm.test.junit)
            }
        }
    }
}
