plugins {
    id("theoneclick.android.library")
    id("theoneclick.jvm.library")
    id("theoneclick.wasm.library")
}

androidLibrary {
    namespace.set("theoneclick.shared.dispatchers")
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
