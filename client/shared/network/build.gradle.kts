plugins {
    id("theoneclick.wasm.library")
    id("theoneclick.android.library")
}

androidLibrary {
    namespace.set("theoneclick.client.shared.network")
    compileSdkVersion.set(libs.versions.android.api.get().toInt())
    minSdkVersion.set(libs.versions.android.api.get().toInt())
    composeEnabled.set(false)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kmp.coroutines)
                implementation(libs.kmp.ktor.client.core)
                implementation(libs.kmp.ktor.client.auth)
                implementation(libs.kmp.ktor.client.content.negotiation)
                implementation(libs.kmp.ktor.client.logging)
                implementation(libs.kmp.ktor.serialization.kotlinx.json)
                implementation(libs.kmp.datetime)
                implementation(projects.shared.base)
                implementation(projects.shared.dispatchers)
                implementation(projects.shared.timeProvider)
                implementation(projects.client.shared.navigation)

                api(libs.kmp.atomicfu)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.android.datastore)
                implementation(libs.jvm.ktor.client.okhttp)
            }
        }
    }
}