plugins {
    id("theoneclick.wasm.library")
    id("theoneclick.android.library")
    id("theoneclick.ios.library")
    alias(libs.plugins.kmp.compose.compiler)
    alias(libs.plugins.kmp.compose.jetbrains)
    alias(libs.plugins.kmp.serialization)
}

androidLibrary {
    namespace = "theoneclick.client.shared.navigation"
    compileSdkVersion = libs.versions.android.api.get().toInt()
    minSdkVersion = libs.versions.android.api.get().toInt()
    composeEnabled = true
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(compose.runtime)
                implementation(libs.kmp.navigation)
                implementation(projects.shared.logging)
            }
        }
    }
}
