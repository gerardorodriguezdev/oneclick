plugins {
    id("theoneclick.wasm.library")
    id("theoneclick.android.library")
    id("theoneclick.ios.library")
}

androidLibrary {
    namespace.set("theoneclick.client.shared.notifications")
    compileSdkVersion.set(libs.versions.android.api.get().toInt())
    minSdkVersion.set(libs.versions.android.api.get().toInt())
    composeEnabled.set(false)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kmp.coroutines)
            }
        }
    }
}