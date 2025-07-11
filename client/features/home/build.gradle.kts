plugins {
    id("theoneclick.wasm.library")
    id("theoneclick.android.library")
    id("theoneclick.ios.library")
    alias(libs.plugins.kmp.compose.compiler)
    alias(libs.plugins.kmp.compose.jetbrains)
    alias(libs.plugins.kmp.serialization)
    alias(libs.plugins.ksp)
}

androidLibrary {
    namespace.set("theoneclick.client.feature.home")
    compileSdkVersion.set(libs.versions.android.api.get().toInt())
    minSdkVersion.set(libs.versions.android.api.get().toInt())
    composeEnabled.set(true)
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
                implementation(projects.shared.dispatchers)
                implementation(projects.shared.timeProvider)
                implementation(projects.shared.logging)
                implementation(projects.client.shared.di)
                implementation(projects.client.shared.network)
                implementation(projects.client.shared.navigation)
                implementation(projects.client.shared.ui)
                implementation(projects.client.shared.notifications)

                project.dependencies {
                    kspAndroid(libs.ksp.kotlin.inject)
                    kspWasmJs(libs.ksp.kotlin.inject)
                    kspIosX64(libs.ksp.kotlin.inject)
                    kspIosArm64(libs.ksp.kotlin.inject)
                    kspIosSimulatorArm64(libs.ksp.kotlin.inject)
                }
            }
        }
    }
}