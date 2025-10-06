plugins {
    id("oneclick.wasm.library")
    id("oneclick.android.library")
    id("oneclick.ios.library")
    alias(libs.plugins.kmp.ksp)
}

androidLibrary {
    namespace = "oneclick.client.shared.di"
    compileSdkVersion = libs.versions.android.api.get().toInt()
    minSdkVersion = libs.versions.android.api.get().toInt()
    composeEnabled = false
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kmp.kotlin.inject)
                implementation(libs.kmp.kotlin.inject.kmp)
                implementation(ktorLibs.client.core)
                implementation(projects.shared.dispatchers)
                implementation(projects.shared.timeProvider)
                implementation(projects.client.shared.network)
                implementation(projects.client.shared.navigation)
                implementation(projects.client.shared.notifications)
                implementation(projects.shared.logging)

                project.dependencies {
                    kspAndroid(libs.gradle.ksp.kotlin.inject)
                    kspWasmJs(libs.gradle.ksp.kotlin.inject)
                    kspIosX64(libs.gradle.ksp.kotlin.inject)
                    kspIosArm64(libs.gradle.ksp.kotlin.inject)
                    kspIosSimulatorArm64(libs.gradle.ksp.kotlin.inject)
                }
            }
        }
    }
}
