plugins {
    id("theoneclick.wasm.library")
    id("theoneclick.android.library")
    id("theoneclick.ios.library")
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
                implementation(ktorLibs.client.core)
                implementation(ktorLibs.client.auth)
                implementation(ktorLibs.client.contentNegotiation)
                implementation(ktorLibs.client.logging)
                implementation(ktorLibs.client.encoding)
                implementation(ktorLibs.serialization.kotlinx.json)
                implementation(libs.kmp.coroutines)
                implementation(libs.kmp.datastore)
                implementation(libs.kmp.datetime)
                implementation(projects.shared.contracts.core)
                implementation(projects.shared.dispatchers)
                implementation(projects.shared.timeProvider)
                implementation(projects.shared.logging)
                implementation(projects.client.shared.navigation)

                api(libs.kmp.atomicfu)
            }
        }

        iosMain {
            dependencies {
                implementation(ktorLibs.client.darwin)
            }
        }

        androidMain {
            dependencies {
                implementation(ktorLibs.client.okhttp)
            }
        }
    }
}
