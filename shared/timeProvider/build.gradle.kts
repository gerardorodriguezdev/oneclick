plugins {
    id("theoneclick.android.library")
    id("theoneclick.jvm.library")
    id("theoneclick.wasm.library")
}

jvmLibrary {
    jvmTarget.set(libs.versions.jvm.api.get().toInt())
}

androidLibrary {
    jvmTarget.set(libs.versions.jvm.api.get().toInt())
    namespace.set("theoneclick.shared.timeProvider")
    compileSdkVersion.set(libs.versions.android.api.get().toInt())
    minSdkVersion.set(libs.versions.android.api.get().toInt())
    composeEnabled.set(false)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kmp.datetime)
            }
        }
    }
}
