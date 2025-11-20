plugins {
    id("oneclick.wasm.library")
    id("oneclick.android.library")
    id("oneclick.ios.library")
    alias(libs.plugins.kmp.compose.compiler)
    alias(libs.plugins.kmp.compose.jetbrains)
    alias(libs.plugins.kmp.serialization)
    alias(libs.plugins.kmp.ksp)
    alias(libs.plugins.kmp.stability.analyzer)
}

androidLibrary {
    namespace = "oneclick.client.feature.home"
    compileSdkVersion = libs.versions.android.api.get().toInt()
    minSdkVersion = libs.versions.android.api.get().toInt()
    composeEnabled = true
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.kmp.coroutines)
                implementation(libs.kmp.navigation)
                implementation(libs.kmp.viewModel)
                implementation(ktorLibs.serialization.kotlinx.json)
                implementation(ktorLibs.client.core)
                implementation(libs.kmp.immutable)
                implementation(libs.kmp.kotlin.inject)
                implementation(libs.kmp.kotlin.inject.kmp)
                implementation(projects.shared.contracts.core)
                implementation(projects.shared.contracts.homes)
                implementation(projects.shared.dispatchers)
                implementation(projects.shared.timeProvider)
                implementation(projects.shared.logging)
                implementation(projects.client.shared.network)
                implementation(projects.client.apps.user.navigation)
                implementation(projects.client.apps.user.di)
                implementation(projects.client.apps.user.ui)
                implementation(projects.client.apps.user.notifications)

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
