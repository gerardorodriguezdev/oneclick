plugins {
    id("oneclick.wasm.library")
    id("oneclick.android.library")
    id("oneclick.ios.library")
    id("oneclick.jvm.library")
}

androidLibrary {
    namespace = "oneclick.client.shared.network"
    compileSdkVersion = libs.versions.android.api.get().toInt()
    minSdkVersion = libs.versions.android.api.get().toInt()
    composeEnabled = false
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
                implementation(projects.shared.contracts.core)
                implementation(projects.shared.contracts.auth)
                implementation(projects.shared.dispatchers)
                implementation(projects.shared.timeProvider)
                implementation(projects.shared.logging)
                implementation(projects.shared.network)
            }
        }

        iosMain {
            dependencies {
                implementation(ktorLibs.client.darwin)
                implementation(libs.kmp.datastore)
            }
        }

        androidMain {
            dependsOn(jvmMain.get())
        }

        jvmMain {
            dependencies {
                implementation(ktorLibs.client.okhttp)
                implementation(libs.kmp.datastore)
            }
        }
    }
}
