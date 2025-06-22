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
                implementation(ktorLibs.client.core)
                implementation(ktorLibs.client.auth)
                implementation(ktorLibs.client.contentNegotiation)
                implementation(ktorLibs.client.logging)
                implementation(ktorLibs.client.encoding)
                implementation(ktorLibs.serialization.kotlinx.json)
                implementation(libs.kmp.datetime)
                implementation(projects.shared.contracts.core)
                implementation(projects.shared.dispatchers)
                implementation(projects.shared.timeProvider)
                implementation(projects.shared.logging)
                implementation(projects.client.shared.navigation)

                api(libs.kmp.atomicfu)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.android.datastore)
                implementation(ktorLibs.client.okhttp)
            }
        }
    }
}