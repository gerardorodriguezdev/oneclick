plugins {
    id("theoneclick.wasm.library")
    id("theoneclick.android.library")
    alias(libs.plugins.kmp.compose.compiler)
    alias(libs.plugins.kmp.compose.jetbrains)
}

androidLibrary {
    namespace.set("theoneclick.client.shared.navigation")
    compileSdkVersion.set(libs.versions.android.api.get().toInt())
    minSdkVersion.set(libs.versions.android.api.get().toInt())
    composeEnabled.set(false)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(compose.runtime)
                implementation(libs.kmp.navigation)
                implementation(projects.shared.base)
            }
        }
    }
}