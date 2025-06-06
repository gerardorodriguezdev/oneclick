plugins {
    id("theoneclick.wasm.library")
    id("theoneclick.android.library")
    alias(libs.plugins.ksp)
}

androidLibrary {
    namespace.set("theoneclick.client.shared.di")
    compileSdkVersion.set(libs.versions.android.api.get().toInt())
    minSdkVersion.set(libs.versions.android.api.get().toInt())
    composeEnabled.set(false)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kmp.kotlin.inject)
                implementation(libs.kmp.kotlin.inject.kmp)
                implementation(libs.kmp.ktor.client.core)
                implementation(projects.shared.core)
                implementation(projects.shared.dispatchers)
                implementation(projects.shared.timeProvider)
                implementation(projects.client.shared.network)
                implementation(projects.client.shared.navigation)

                project.dependencies {
                    kspCommonMainMetadata(libs.ksp.kotlin.inject)
                    kspAndroid(libs.ksp.kotlin.inject)
                    kspWasmJs(libs.ksp.kotlin.inject)
                }
            }
        }
    }
}