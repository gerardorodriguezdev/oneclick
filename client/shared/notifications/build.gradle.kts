plugins {
    id("theoneclick.wasm.library")
    id("theoneclick.android.library")
    id("theoneclick.ios.library")
}

androidLibrary {
    namespace = "theoneclick.client.shared.notifications"
    compileSdkVersion = libs.versions.android.api.get().toInt()
    minSdkVersion = libs.versions.android.api.get().toInt()
    composeEnabled = false
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
